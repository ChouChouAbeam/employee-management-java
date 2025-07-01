package my.company.handlers;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.EventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.request.UserInfo;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CdsUpsertEventContext;
import com.sap.cds.services.persistence.PersistenceService;

import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.ql.Select;

import cds.gen.employeesservice.Employees;
import cds.gen.employeesservice.EmployeesService_;
import cds.gen.employeesservice.Employees_;
import cds.gen.employeesservice.GetUserInfoContext;
import cds.gen.employeesservice.Roles_;

@Component
@ServiceName(EmployeesService_.CDS_NAME)
public class EmployeesServiceHandler implements EventHandler {
  @Autowired
  UserInfo userInfo;

  @Autowired
  PersistenceService persistenceService;

  private static final int BONUS_PER_YEAR = 1000;

  private static final Logger logger = LoggerFactory.getLogger(EmployeesServiceHandler.class);

  @Before(event = { CqnService.EVENT_CREATE,
      CqnService.EVENT_UPSERT,
      CqnService.EVENT_UPDATE }, entity = Employees_.CDS_NAME)
  public void calculateSalary(EventContext ctx) {
    // Cast to appropriate context types to access data
    if (ctx instanceof CdsCreateEventContext) {
      CdsCreateEventContext createCtx = (CdsCreateEventContext) ctx;
      createCtx.getCqn().entries().forEach(this::updateSalaryWithBonus);
    } else if (ctx instanceof CdsUpdateEventContext) {
      CdsUpdateEventContext updateCtx = (CdsUpdateEventContext) ctx;
      updateCtx.getCqn().entries().forEach(this::updateSalaryWithBonus);
    } else if (ctx instanceof CdsUpsertEventContext) {
      CdsUpsertEventContext upsertCtx = (CdsUpsertEventContext) ctx;
      upsertCtx.getCqn().entries().forEach(this::updateSalaryWithBonus);
    }
  }

  private void updateSalaryWithBonus(Map<String, Object> entry) {
    Object hireDateObj = entry.get(Employees.HIRE_DATE);
    Object roleIdObj = entry.get(Employees.ROLE_ID);

    if (hireDateObj != null && roleIdObj != null) {
      LocalDate hireDate = LocalDate.parse(hireDateObj.toString());
      String roleId = roleIdObj.toString();
      
      // Fetch base salary from role in database
      BigDecimal baseSalary = getBaseSalaryFromRole(roleId);
      
      if (baseSalary != null) {
        // Calculate years of experience
        int currentYear = LocalDate.now().getYear();
        int hireYear = hireDate.getYear();
        int yearsOfExperience = Math.max(0, currentYear - hireYear); // Ensure non-negative
        
        // Calculate bonus: years of experience * BONUS_PER_YEAR
        BigDecimal bonus = BigDecimal.valueOf(yearsOfExperience * BONUS_PER_YEAR);
        
        // Calculate total salary: base salary + bonus
        BigDecimal totalSalary = baseSalary.add(bonus);
                
        // Update the salary field
        entry.put(Employees.SALARY, totalSalary);

        logger.info("Updated entry: {}", entry);
      } else {
        logger.warn("Could not find base salary for role ID: {}", roleId);
      }
    }
  }

  private BigDecimal getBaseSalaryFromRole(String roleId) {
    try {
      // Query the role to get the base salary
      var result = persistenceService.run(
        Select.from(Roles_.CDS_NAME)
              .columns("baseSalary")
              .where(r -> r.get("ID").eq(roleId))
      );
      
      if (result.first().isPresent()) {
        var row = result.first().get();
        Object baseSalaryObj = row.get("baseSalary");
        if (baseSalaryObj != null) {
          return new BigDecimal(baseSalaryObj.toString());
        }
      }
    } catch (Exception e) {
      logger.error("Error fetching base salary for role ID: {}", roleId, e);
    }
    
    return null;
  }

  @On(event = { GetUserInfoContext.CDS_NAME })
  public void getUserInfo(GetUserInfoContext context) {
    String userId = userInfo.getName();
    Boolean authenticated = userInfo.isAuthenticated();
    Collection<String> roles = userInfo.getRoles();

    Map<String, Integer> rolesMap = new HashMap<>();
    if (roles != null) {
      for (String role : roles) {
        rolesMap.put(role, 1);
      }
    }

    Map<String, Object> userInfoMap = new HashMap<>();
    userInfoMap.put(GetUserInfoContext.ReturnType.USER_ID, userId != null ? userId : "unknown");
    userInfoMap.put(GetUserInfoContext.ReturnType.AUTHENTICATED, authenticated != null ? authenticated : false);
    userInfoMap.put(GetUserInfoContext.ReturnType.ROLES, rolesMap);

    GetUserInfoContext.ReturnType typedResult = GetUserInfoContext.ReturnType.of(userInfoMap);
    context.setResult(typedResult);
  }

}

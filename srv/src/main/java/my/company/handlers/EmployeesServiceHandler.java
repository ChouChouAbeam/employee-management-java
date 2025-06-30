package my.company.handlers;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.EventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.request.UserInfo;

import com.sap.cds.services.handler.annotations.ServiceName;
import cds.gen.employeesservice.EmployeesService_;
import cds.gen.employeesservice.Employees_;
import cds.gen.employeesservice.GetUserInfoContext;

@Component
@ServiceName(EmployeesService_.CDS_NAME)
public class EmployeesServiceHandler implements EventHandler {
  @Autowired
  UserInfo userInfo;

  private static final int BONUS_PER_YEAR = 1000;

  EmployeesServiceHandler() {}

  @Before(event = { CqnService.EVENT_CREATE,
      CqnService.EVENT_UPSERT,
      CqnService.EVENT_UPDATE }, entity = Employees_.CDS_NAME)
  public void calculateSalary(EventContext ctx) {
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

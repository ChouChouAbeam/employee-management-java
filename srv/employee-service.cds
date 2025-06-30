using my.company as my from '../db/data-model';

@requires: 'authenticated-user'
@restrict: [
    {
        grant: '*',
        to   : 'admin'
    },
    {
        grant: 'READ',
        to   : 'viewer'
    }
]
service EmployeesService {
    entity Employees   as projection on my.Employees;
    entity Roles       as projection on my.Roles;
    entity Departments as projection on my.Departments;

    type RolesType {
        admin  : Integer;
        viewer : Integer;
    }

    function getUserInfo() returns {
        userId        : String;
        authenticated : Boolean;
        roles         : RolesType;
    };
}

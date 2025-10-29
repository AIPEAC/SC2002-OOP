package Interface;

import Entity.Application;
import java.util.List;
public interface InterfaceCLI {
    void login(String userID, String password);
    void changePassowrd(String originalPassword, String newPassword);
    default List<Application> filterInternshipApplications(String filterOrder,boolean ascendance,String[] filterOut){
        //Logic...
        return null;
    }

    default void viewFilteredInternshipApplication(String filterOrder,boolean ascendance,String[] filterOut){
        //default filter would be filterOrder="alphabet", ascending=true, filterOut={}
        List<Application> filteredApplications=filterInternshipApplications(filterOrder,ascendance,filterOut);
        //printOut...
    }
}
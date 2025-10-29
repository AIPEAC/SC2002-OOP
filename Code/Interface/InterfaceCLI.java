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

    default void viewFilteredInternshipApplication(String filterOrder="alphabet",boolean ascendance=True,String[] filterOut){
        filterInternshipApplications(filterOrder,ascendance,filterOut)
        //printOut...
    }
}
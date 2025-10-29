package Interface;

import Entity.Application;
import java.util.List;
public interface InterfaceCLI {
    void login(String userID, String password);
    default List<Application> filterInternshipApplications(String filterOrder="alphabet"){
        //
        return null;
    }
    default void viewFilteredInternshipApplication(){
        //
    }
}
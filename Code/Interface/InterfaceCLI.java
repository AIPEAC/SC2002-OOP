package Interface;

import Entity.Application;
import Entity.InternshipOpportunity;
import java.util.List;
public interface InterfaceCLI {
    default void login(String userID, String password){
        //should I pass the logincontrol class as the attr list?
    }

    default void logout() {
        //
    }

    default void changePassword(String originalPassword, String newPassword){
        //
    };

    default List<Application> filterInternshipApplications(String filterOrder,boolean ascendance,String[] filterOut){
        //Logic...
        return null;
    }

    default void viewFilteredInternshipApplications(String filterOrder,boolean ascendance,String[] filterOut){
        //default filter would be filterOrder="alphabet", ascending=true, filterOut={}
        List<Application> filteredApplications=filterInternshipApplications(filterOrder,ascendance,filterOut);
        //printOut...
    }

    default List<InternshipOpportunity> filterInternshipOpportunities(String filterType, boolean ascending, String[] filterOptions) {
        //
        return null;
    }

    default void viewFilteredInternshipOpportunities(String filterType, boolean ascending, String[] filterOptions) {
        //
    }
}
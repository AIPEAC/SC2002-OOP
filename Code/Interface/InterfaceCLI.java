package Interface;
public interface InterfaceCLI {
    void login(String userID, String password);
    default List<Application> filterInternshipApplications(){

    }
}
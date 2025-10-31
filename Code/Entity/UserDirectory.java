package Entity;

import Entity.Users.*;

public class UserDirectory {
    private Student[] studentList;
    private CareerStaff[] staffList;
    private CompanyRepresentative[] companyRepList;

    private void createLoginListFromDB(){
        //
    }

    /*
     * the following 3 function is supposed to use only after the user login 
     * and we know which user they are
     * so the memory would not blow up if there are too many of them
     */


    private void createStudentListFromDB(String userID){
        //
    }
    private void createStaffListFromDB(String userID){
        //
    }
    private void createCompanyRepListFromDB(String userID){
        //
    }
    
    public UserDirectory() {
        createLoginListFromDB();
    }
    public User verifyUser(String userID, String password){
        
        return null;
        
    }
}

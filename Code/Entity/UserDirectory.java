package Entity;

import Entity.Users.*;

public class UserDirectory {
    private Student[] studentList;
    private CareerStaff[] staffList;
    private CompanyRepresentative[] companyRepList;

    private void createStudentListFromDB(){
        //
    }
    private void createStaffListFromDB(){
        //
    }
    private void createCompanyRepListFromDB(){
        //
    }
    
    public UserDirectory() {
        createStudentListFromDB();
        createStaffListFromDB();
        createCompanyRepListFromDB();
    }
    public User verifyUser(String userID, String password){
        
        return null;
        
    }
}

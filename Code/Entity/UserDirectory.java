package Entity;

import Entity.Users.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserDirectory {
    private User currentUser;
    private List<String[]> loginList;
    

    private void loadLoginListFromDB(){
        String csvFile = "Code/Lib/login_list.csv";
        String line = "";
        String cvsSplitBy = ",";
        loginList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] loginData = line.split(cvsSplitBy);
                loginList.add(loginData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * the following 3 function is supposed to use only after the user login 
     * and we know which user they are
     * so the memory would not blow up if there are too many of them
     */


    private void loadStudentListFromDB(String userID){
        //
    }
    private void loadStaffListFromDB(String userID){
        //
    }
    private void loadCompanyRepListFromDB(String userID){
        //
    }
    
    public UserDirectory() {
        loadLoginListFromDB();
    }
    public User verifyUser(String userID, String password){
        
        return null;
        
    }
}
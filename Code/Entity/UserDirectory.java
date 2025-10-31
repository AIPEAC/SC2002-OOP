package Entity;

import Entity.Users.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserDirectory {
    
    private List<String[]> loginList;
    
    public UserDirectory() {
        loadLoginListFromDB();
    }

    private void loadLoginListFromDB(){
        String csvFile = "Code/Lib/login_list.csv";
        File file = new File(csvFile);
        String line = "";
        String cvsSplitBy = ",";
        loginList = new ArrayList<>();

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); 
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.append("userID,passwordHash,salt\n");
                }
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] loginData = line.split(cvsSplitBy);
                    loginList.add(loginData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * the following 3 function is supposed to use only after the user login 
     * and we know which user they are
     * e.g. we only create a student instance if the userID belongs to 
     * so the memory would not blow up if there are too many of them
     */


    private void loadStudentFromDB(String userID){
        //
    }
    private void loadStaffFromDB(String userID){
        //
    }
    private void loadCompanyRepFromDB(String userID){
        //
    }
    
    
    public User verifyUser(String userID, String password){
        
        return null;
        
    }
}

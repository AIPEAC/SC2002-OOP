package Entity;

import Entity.Users.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class UserLoginDirectory{
    private List<String[]> loginList;
    
    public UserLoginDirectory() {
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
        for (String[] loginData : loginList) {
            if (loginData[0].equals(userID) ) {
                
            }
        }
            


        System.out.println("userID or password incorrect.");
        return null;
        
           
    }

    private static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }
}

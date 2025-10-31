package Control;

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

public class UserLoginDirectoryControl{
    private List<String[]> loginList;
    private List<String[]> StudentList;
    private List<String[]> StaffList;
    private List<String[]> CompanyRepList;
    
    public UserLoginDirectoryControl() {
        loadLoginListFromDB();
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
                    writer.append("identity,userID,passwordHash,salt\n");
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
    private void loadStudentFromDB(String userID){
        String csvFile = "Code/Lib/student.csv";
        File file = new File(csvFile);
        String line = "";
        String cvsSplitBy = ",";
        StudentList = new ArrayList<>();

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); 
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.append("userID,name,email,major,year,hasAcceptedInternshipOpportunity\n");
                }
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] StudentData = line.split(cvsSplitBy);
                    StudentList.add(StudentData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadStaffFromDB(String userID){
        String csvFile = "Code/Lib/staff.csv";
        File file = new File(csvFile);
        String line = "";
        String cvsSplitBy = ",";
        StaffList = new ArrayList<>();

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); 
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.append("userID,name,email,department,role\n");
                }
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] StaffData = line.split(cvsSplitBy);
                    StaffList.add(StaffData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadCompanyRepFromDB(String userID){
        String csvFile = "Code/Lib/company_representative.csv";
        File file = new File(csvFile);
        String line = "";
        String cvsSplitBy = ",";
        CompanyRepList = new ArrayList<>();

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); 
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.append("userID,name,email,position,accountStatus,companyName,department\n");
                }
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] CompanyRepData = line.split(cvsSplitBy);
                    CompanyRepList.add(CompanyRepData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public String verifyUser(String userID, String password){
        for (String[] loginData : loginList) {
            if (loginData[1].equals(userID) && loginData[2].equals(hashPassword(password))) {
                String identity=loginData[0];
                System.out.println("login successfully");
                return identity;
            }
        }
        System.out.println("userID or password incorrect.");
        return null;        
    }

    
    public User createUser(String userID, String identity){
        switch(identity){
            case "Student":
                loadStudentFromDB(userID);
                break;
            case "Staff":
                loadStaffFromDB(userID);
                break;
            case "CompanyRepresentative":
                loadCompanyRepFromDB(userID);
                break;
            default:
                System.out.println("bug: UserLoginDirectory.createUser(): wrong indentity, possibly wrongly writen into login_list.csv");
                return null;
        }
        return null;
    }
                
 
    
}

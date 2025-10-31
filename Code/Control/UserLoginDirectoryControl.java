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
    private String[] StudentInfoList;
    private String[] StaffInfoList;
    private String[] CompanyRepInfoList;
    
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
        String csvSplitBy = ",";
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
                    String[] loginData = line.split(csvSplitBy);
                    loginList.add(loginData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadStudent(String userID){
        String csvFile = "Code/Lib/student.csv";
        File file = new File(csvFile);
        String line = "";
        String csvSplitBy = ",";

        try {
            if (!file.exists()) {
                throw new Exception("bug: UserLoginDirectoryControl.loadStudent(): The data file is not found. Initialization fail or data file deleted.");
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] StudentData = line.split(csvSplitBy);
                    if (StudentData[0].equals(userID)) {
                        StudentInfoList = StudentData;
                        break;
                    }
                }
                if (StudentInfoList == null) {
                    throw new Exception("bug: UserLoginDirectoryControl.loadStudent(): Student not found. code for login is wrong.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void loadStaff(String userID){
        String csvFile = "Code/Lib/staff.csv";
        File file = new File(csvFile);
        String line = "";
        String csvSplitBy = ",";

        try {
            if (!file.exists()) {
                throw new Exception("bug: UserLoginDirectoryControl.loadStaff(): The data file is not found. Initialization fail or data file deleted.");
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] StaffData = line.split(csvSplitBy);
                    if (StaffData[0].equals(userID)) {
                        StaffInfoList = StaffData;
                        break;
                    }
                }
                if (StaffInfoList == null) {
                    throw new Exception("bug: UserLoginDirectoryControl.loadStaff(): Staff not found. code for login is wrong.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void loadCompanyRep(String userID){
        String csvFile = "Code/Lib/company_representative.csv";
        File file = new File(csvFile);
        String line = "";
        String csvSplitBy = ",";

        try {
            if (!file.exists()) {
                throw new Exception("bug: UserLoginDirectoryControl.loadCompanyRep(): The data file is not found. Initialization fail or data file deleted.");
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] CompanyRepData = line.split(csvSplitBy);
                    if (CompanyRepData[0].equals(userID)) {
                        CompanyRepInfoList = CompanyRepData;
                        break;
                    }
                }
                if (CompanyRepInfoList == null) {
                    throw new Exception("bug: UserLoginDirectoryControl.loadCompanyRep(): Company Representative not found. code for login is wrong.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }  
    
    public String verifyUser(String userID, String password){
        for (String[] loginData : loginList) {
            if (loginData[1].equals(userID) && loginData[2].equals(hashPassword(password))) {
                String identity=loginData[0];
                if (identity.equals("CompanyRepresentative")) {
                    loadCompanyRep(userID);
                    if (CompanyRepInfoList != null) {
                        String status = CompanyRepInfoList[4];
                        CompanyRepInfoList = null;
                        switch (status) {
                            case "approved":
                                return identity;
                            case "pending":
                                return "ACCOUNT_PENDING";
                            case "rejected":
                                return "ACCOUNT_REJECTED";
                            default:
                                return null; // Or some other error status
                        }
                    }
                }
                return identity;
            }
        }
        return null;
    }

    
    public User createUser(String userID, String identity){
        switch(identity){
            case "Student":
                loadStudent(userID);
                Student student=new Student(StudentInfoList[0],
                    StudentInfoList[1],
                    StudentInfoList[2],
                    StudentInfoList[3],
                    Integer.parseInt(StudentInfoList[4]),
                    Boolean.parseBoolean(StudentInfoList[5]));
                StudentInfoList=null;
                return student;
            case "Staff":
                loadStaff(userID);
                CareerStaff staff = new CareerStaff(StaffInfoList[0],
                    StaffInfoList[1],
                    StaffInfoList[2],
                    StaffInfoList[3],
                    StaffInfoList[4]);
                StaffInfoList=null;
                return staff;
            case "CompanyRepresentative":
                loadCompanyRep(userID);
                CompanyRepresentative companyRep = new CompanyRepresentative(CompanyRepInfoList[0],
                    CompanyRepInfoList[1],
                    CompanyRepInfoList[2],
                    CompanyRepInfoList[3],
                    CompanyRepInfoList[4],
                    CompanyRepInfoList[5],
                    CompanyRepInfoList[6]);
                CompanyRepInfoList=null;
                return companyRep;
            default:
                System.out.println("bug: UserLoginDirectory.createUser(): wrong indentity, possibly wrongly writen into login_list.csv");
                return null;
        }
    }
                
    public void changePassword(String userID, String newPassword){
        for (String[] loginData : loginList) {
            if (loginData[1].equals(userID)) {
                loginData[2] = hashPassword(newPassword);
                break;
            }
        }

        String csvFile = "Code/Lib/login_list.csv";
        File inputFile = new File(csvFile);
        File tempFile = new File("Code/Lib/login_list.tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {

            writer.append("identity,userID,passwordHash,salt\n");
            for (String[] loginData : loginList) {
                writer.append(String.join(",", loginData));
                writer.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }
    
    public String requestRegisterCompanyRep(String name,String companyName,String department,String postion,String email){
        
        String assignedID=assignIDToCompanyRep();

        try (FileWriter writer = new FileWriter("Code/Lib/company_representative.csv", true)) {
            writer.append(String.join(",", assignedID, name, email, postion, "pending", companyName, department));
            writer.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try (FileWriter writer = new FileWriter("Code/Lib/login_list.csv", true)) {
            writer.append(String.join(",", "CompanyRepresentative", assignedID, hashPassword("password"), ""));
            writer.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        loadLoginListFromDB();

        return assignedID;
    }
    private String assignIDToCompanyRep(){
        List<String> allUserIDs = new ArrayList<>();
        String[] csvFiles = {"Code/Lib/company_representative.csv", "Code/Lib/student.csv", "Code/Lib/staff.csv"};

        for (String csvFile : csvFiles) {
            File file = new File(csvFile);
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    try (FileWriter writer = new FileWriter(file)) {
                        String header = "";
                        if (csvFile.equals("Code/Lib/company_representative.csv")) {
                            header = "userID,name,email,position,accountStatus,companyName,department";
                        } else if (csvFile.equals("Code/Lib/student.csv")) {
                            header = "userID,name,email,major,year,hasAcceptedInternshipOpportunity";
                        } else if (csvFile.equals("Code/Lib/staff.csv")) {
                            header = "userID,name,email,department,role";
                        }
                        writer.append(header);
                        writer.append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length > 0) {
                        allUserIDs.add(data[0]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int maxID = 0;
        for (String userID : allUserIDs) {
            if (userID.startsWith("comprep")) {
                try {
                    int idNum = Integer.parseInt(userID.substring(7));
                    if (idNum > maxID) {
                        maxID = idNum;
                    }
                } catch (NumberFormatException e) {
                    // Ignore IDs that don't have a valid number format
                    continue;
                }
            }
        }

        return String.format("comprep%04d", maxID + 1);
    }

    
}

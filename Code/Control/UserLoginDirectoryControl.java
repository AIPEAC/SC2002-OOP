package Control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;

import Control.Tool.ControlUtils;
import Entity.Users.*;

import java.util.Arrays;

/**
 * Manages user authentication and user account operations for the system.
 * This class handles login verification, password management with SHA-256 hashing and salting,
 * and company representative registration with email-based identification.
 * <p>
 * The class supports initialization of user data from sample CSV files and manages the
 * login directory, which stores credentials for all user types (Students, Staff, Company Representatives).
 * Each user type has a separate CSV data file containing their detailed information.
 * </p>
 * <p>
 * All CSV read/write operations use proper field escaping to handle special characters
 * (commas, quotes, newlines) in user names, company names, departments, and positions,
 * ensuring data integrity. Sample files are read in simple CSV format and converted to
 * properly escaped format when written to the database.
 * </p>
 * 
 * @author Allen
 * @version 2.0
 */
public class UserLoginDirectoryControl{
    private List<String[]> loginList;
    private String[] StudentInfoList;
    private String[] StaffInfoList;
    private String[] CompanyRepInfoList;
    private boolean haveInitialized=false;
    private AuthenticationControl authCtrl;
    
    /**
     * Constructs a UserLoginDirectoryControl with the specified authentication control.
     * Initializes user data from example files and loads the login list from database.
     * 
     * @param authCtrl The authentication control instance for coordinating user authentication
     */
    UserLoginDirectoryControl(AuthenticationControl authCtrl){
        this.authCtrl=authCtrl;
        loadUsersFromExamplesToDB();
        loadLoginListFromDB();
    }

    private void loadLoginListFromDB(){
        String csvFile = "Code/Backend/Lib/login_list.csv";
        File file = new File(csvFile);
        String line = "";
        loginList = new ArrayList<>();

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); 
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.append("identity,userID,passwordHash,salt,status\n");
                }
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    // Use proper CSV parsing that respects quoted fields
                    String[] loginData = ControlUtils.splitCsvLine(line);
                    // Unescape fields
                    for (int i = 0; i < loginData.length; i++) {
                        loginData[i] = ControlUtils.unescapeCsvField(loginData[i]);
                    }
                    loginList.add(loginData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadStudent(String userID){
        String csvFile = "Code/Backend/Lib/student.csv";
        File file = new File(csvFile);
        String line = "";

        try {
            if (!file.exists()) {
                throw new Exception("bug: UserLoginDirectoryControl.loadStudent(): The data file is not found. Initialization fail or data file deleted.");
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] StudentData = ControlUtils.splitCsvLine(line);
                    // Unescape fields
                    for (int i = 0; i < StudentData.length; i++) {
                        StudentData[i] = ControlUtils.unescapeCsvField(StudentData[i]);
                    }
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
            throw new RuntimeException(e.getMessage());
        }
    }
    private void loadStaff(String userID){
        String csvFile = "Code/Backend/Lib/staff.csv";
        File file = new File(csvFile);
        String line = "";

        try {
            if (!file.exists()) {
                throw new Exception("bug: UserLoginDirectoryControl.loadStaff(): The data file is not found. Initialization fail or data file deleted.");
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] StaffData = ControlUtils.splitCsvLine(line);
                    // Unescape fields
                    for (int i = 0; i < StaffData.length; i++) {
                        StaffData[i] = ControlUtils.unescapeCsvField(StaffData[i]);
                    }
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
            throw new RuntimeException(e.getMessage());
        }
    }
    private void loadCompanyRep(String userID){
        String csvFile = "Code/Backend/Lib/company_representative.csv";
        File file = new File(csvFile);
        String line = "";

        try {
            if (!file.exists()) {
                throw new Exception("bug: UserLoginDirectoryControl.loadCompanyRep(): The data file is not found. Initialization fail or data file deleted.");
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                while ((line = br.readLine()) != null) {
                    String[] CompanyRepData = ControlUtils.splitCsvLine(line);
                    // Unescape fields
                    for (int i = 0; i < CompanyRepData.length; i++) {
                        CompanyRepData[i] = ControlUtils.unescapeCsvField(CompanyRepData[i]);
                    }
                    // Normalize to 7 columns: userID,name,email,position,accountStatus,companyName,department
                    if (CompanyRepData.length < 7) {
                        String[] expanded = new String[7];
                        for (int i = 0; i < CompanyRepData.length; i++) expanded[i] = CompanyRepData[i];
                        for (int i = CompanyRepData.length; i < 7; i++) expanded[i] = "";
                        CompanyRepData = expanded;
                    }
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
            throw new RuntimeException(e.getMessage());
        }
    }  
    
    String verifyUser(String userID, String password){
        for (String[] loginData : loginList) {
            String identity = loginData[0];
            if (loginData[1].equals(userID)) {
                String salt = "";
                if (loginData.length > 3 && loginData[3] != null) salt = loginData[3];
                if (!loginData[2].equals(hashPassword(password, salt))) {
                    // password mismatch
                    continue;
                }
                if (identity.equals("CompanyRepresentative")) {
                    loadCompanyRep(userID);
                    if (CompanyRepInfoList != null) {
                        String status = CompanyRepInfoList[4];
                        CompanyRepInfoList = null;
                        switch (status) {
                            case "approved":
                                return identity;
                            case "pending":
                                return "pending";
                            case "rejected":
                                return "rejected";
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
     
    
    // New: SHA-256 hash with salt. If salt is empty string, behaves like previous hashPassword.
    private static String hashPassword(String password, String salt) {
        if (password == null) return null;
        String input = (salt == null || salt.isEmpty()) ? password : salt + password;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
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

    // Generate a random salt encoded in base64-url without padding
    private static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(salt);
    }
    User createUser(String userID, String identity){
        switch(identity){
            case "Student":
                loadStudent(userID);
                List<String> majors = null;
                if (StudentInfoList[3] != null && !StudentInfoList[3].isEmpty()) {
                    // Majors are separated by semicolons only (not spaces, as major names can contain spaces)
                    if (StudentInfoList[3].contains(";")) {
                        majors = Arrays.asList(StudentInfoList[3].split(";"));
                        // Trim each major
                        majors = majors.stream().map(String::trim).filter(s -> !s.isEmpty()).collect(java.util.stream.Collectors.toList());
                    } else {
                        // Single major (no semicolon separator)
                        majors = Arrays.asList(StudentInfoList[3].trim());
                    }
                }
                Student student=new Student(StudentInfoList[0],
                    StudentInfoList[1],
                    StudentInfoList[2],
                    majors,
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
                throw new IllegalArgumentException("bug: UserLoginDirectory.createUser(): wrong identity, possibly wrongly written into login_list.csv");
        }
    }
    String getCompanyRepsCompany(String userID){
        if (authCtrl.isLoggedIn()) {
            loadCompanyRep(userID);
            String companyName=CompanyRepInfoList[5];
            CompanyRepInfoList=null;
            return companyName;
        }
        throw new IllegalStateException("UserLoginDirectoryControl.getCompanyRepsCompany(): no logged in user when getting a CompanyRep's company");
    }
    

    void changePassword(String userID, String newPassword){
        if (!authCtrl.isLoggedIn()) {
            throw new IllegalStateException("UserLoginDirectoryControl.changePassword(): no logged in user when changing password");
        }
        for (String[] loginData : loginList) {
            if (loginData[1].equals(userID)) {
                String newSalt = generateSalt();
                // ensure array length to 5 (identity,userID,passwordHash,salt,status)
                for (int i = 0; i < loginList.size(); i++) {
                    String[] row = loginList.get(i);
                    if (row.length > 1 && row[1].equals(userID)) {
                        if (row.length < 5) {
                            String[] expanded = new String[5];
                            for (int j = 0; j < row.length; j++) expanded[j] = row[j];
                            for (int j = row.length; j < 5; j++) expanded[j] = "";
                            row = expanded;
                        }
                        row[3] = newSalt; // salt
                        row[2] = hashPassword(newPassword, newSalt);
                        loginList.set(i, row);
                        break;
                    }
                }
            }
        }

    String csvFile = "Code/Backend/Lib/login_list.csv";
    File inputFile = new File(csvFile);
    File tempFile = new File("Code/Backend/Lib/login_list.tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {

            writer.append("identity,userID,passwordHash,salt,status\n");
            for (String[] loginData : loginList) {
                // normalize to 5 columns and escape each field
                String[] out = new String[] {"", "", "", "", ""};
                for (int k = 0; k < loginData.length && k < 5; k++) {
                    out[k] = (loginData[k] == null) ? "" : loginData[k];
                }
                // Escape all fields when writing
                writer.append(String.join(",",
                    ControlUtils.escapeCsvField(out[0]),
                    ControlUtils.escapeCsvField(out[1]),
                    ControlUtils.escapeCsvField(out[2]),
                    ControlUtils.escapeCsvField(out[3]),
                    ControlUtils.escapeCsvField(out[4])));
                writer.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }
    /**
     * Request to register a new company representative account.
     * All text fields (name, company name, department, position) are properly escaped
     * to handle special characters like commas and quotes when stored in CSV.
     * 
     * @param name the representative's name (can contain commas, titles like "Dr., PhD")
     * @param companyName the company name (can contain commas like "Smith, Johnson &amp; Co.")
     * @param department the department (can contain commas and special characters)
     * @param postion the position title (can contain commas like "VP, Engineering")
     * @param email the email address (used as userID)
     * @return the assigned user ID (email) or null if registration fails
     * @throws IllegalArgumentException if validation fails or duplicates exist
     */
    String requestRegisterCompanyRep(String name,String companyName,String department,String postion,String email){
        // Validate inputs: require at least 3 characters for companyName and name
        if (companyName == null || companyName.trim().length() < 3) {
            throw new IllegalArgumentException("Company name must be at least 3 characters.");
        }
        if (name == null || name.trim().length() < 3) {
            throw new IllegalArgumentException("Name must be at least 3 characters.");
        }
        // Validate email: must be non-empty and follow proper email format
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required for company representative registration.");
        }
        // More robust email pattern: requires at least 1 char before @, at least 1 char for domain, and proper TLD
        // Pattern: one or more alphanumeric/dots/underscores/hyphens, @, domain name, ., TLD (2-6 chars)
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.trim().matches(emailPattern)) {
            throw new IllegalArgumentException("Email must follow a valid format (e.g., user@company.com)");
        }

        // Check duplicates: existing company representative names or company names should not duplicate
        String csvFile = "Code/Backend/Lib/company_representative.csv";
        File file = new File(csvFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                br.readLine(); // skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = ControlUtils.splitCsvLine(line);
                    // Unescape fields
                    for (int i = 0; i < parts.length; i++) {
                        parts[i] = ControlUtils.unescapeCsvField(parts[i]);
                    }
                    if (parts.length >= 2) {
                        String existingName = parts[1] != null ? parts[1].trim() : "";
                        String existingCompany = parts.length > 5 && parts[5] != null ? parts[5].trim() : "";
                        if (!existingName.isEmpty() && existingName.equalsIgnoreCase(name.trim())) {
                            throw new IllegalArgumentException("A company representative with the same name already exists: " + existingName);
                        }
                        if (!existingCompany.isEmpty() && existingCompany.equalsIgnoreCase(companyName.trim())) {
                            throw new IllegalArgumentException("A company with the same name already exists: " + existingCompany);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Use email as the userID for company representatives
        String assignedID = email.trim();

        // Check if email already exists in login list
        for (String[] loginEntry : loginList) {
            if (loginEntry.length > 1 && loginEntry[1].equals(assignedID)) {
                throw new IllegalArgumentException("A user with this email already exists.");
            }
        }

        try (FileWriter writer = new FileWriter("Code/Backend/Lib/company_representative.csv", true)) {
            // Escape fields to handle commas properly
            writer.append(String.join(",", 
                ControlUtils.escapeCsvField(assignedID), 
                ControlUtils.escapeCsvField(name), 
                ControlUtils.escapeCsvField(email), 
                ControlUtils.escapeCsvField(postion), 
                ControlUtils.escapeCsvField("pending"), 
                ControlUtils.escapeCsvField(companyName), 
                ControlUtils.escapeCsvField(department)));
            writer.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try (FileWriter writer = new FileWriter("Code/Backend/Lib/login_list.csv", true)) {
            String salt = generateSalt();
            // store status as pending in the 5th column
            // Escape fields to handle commas properly
            writer.append(String.join(",", 
                ControlUtils.escapeCsvField("CompanyRepresentative"), 
                ControlUtils.escapeCsvField(assignedID), 
                ControlUtils.escapeCsvField(hashPassword("password", salt)), 
                ControlUtils.escapeCsvField(salt), 
                ControlUtils.escapeCsvField("pending")));
            writer.append("\n");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        loadLoginListFromDB();

        return assignedID;
    }
    
    /**
     * @deprecated This method is no longer used. Company representatives now use their email
     * addresses as userIDs instead of auto-generated "comprep####" format.
     * See {@link #requestRegisterCompanyRep(String, String, String, String, String)} which
     * directly uses email as the userID.
     * 
     * Legacy method that auto-generates sequential company representative IDs in the format
     * "comprep0001", "comprep0002", etc. This functionality has been replaced with email-based
     * identification for better traceability and to avoid ID collision issues.
     * 
     * @return a formatted string like "comprep0001" (never called in current implementation)
     */
    
    @SuppressWarnings("unused")
    @Deprecated
    private String assignIDToCompanyRep(){
        List<String> allUserIDs = new ArrayList<>();
        String[] csvFiles = {"Code/Backend/Lib/company_representative.csv", "Code/Backend/Lib/student.csv", "Code/Backend/Lib/staff.csv"};

        for (String csvFile : csvFiles) {
            File file = new File(csvFile);
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    try (FileWriter writer = new FileWriter(file)) {
                        String header = "";
                        if (csvFile.equals("Code/Backend/Lib/company_representative.csv")) {
                            header = "userID,name,email,position,accountStatus,companyName,department";
                        } else if (csvFile.equals("Code/Backend/Lib/student.csv")) {
                            header = "userID,name,email,major,year,hasAcceptedInternshipOpportunity";
                        } else if (csvFile.equals("Code/Backend/Lib/staff.csv")) {
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
                    if (line.trim().isEmpty()) continue;
                    // Use proper CSV parsing that respects quoted fields
                    String[] data = ControlUtils.splitCsvLine(line);
                    if (data.length > 0) {
                        allUserIDs.add(ControlUtils.unescapeCsvField(data[0]));
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

    void approveCompanyRep(String userID){
        if (!authCtrl.isLoggedIn()) throw new IllegalStateException("UserLoginDirectoryControl.approveCompanyRep(): no logged in user when approving a CompanyRep");
        if (userID == null) throw new IllegalArgumentException("UserLoginDirectoryControl.approveCompanyRep(): null userID");
        updateCompanyRepStatusInLogin(userID, "approved");
        updateCompanyRepStatusInCompanyRepCSV(userID, "approved");
    }
    void rejectCompanyRep(String userID){
        updateCompanyRepStatusInLogin(userID, "rejected");
        updateCompanyRepStatusInCompanyRepCSV(userID, "rejected");
    }
    // Update the status field (stored in the 5th column) for a CompanyRepresentative in login_list.csv
    private void updateCompanyRepStatusInLogin(String userID, String status) {
        boolean updated = false;

        // Ensure in-memory list is present
        if (loginList == null) {
            loadLoginListFromDB();
        }

        // Update the in-memory row first
        for (int i = 0; i < loginList.size(); i++) {
            String[] row = loginList.get(i);
            if (row.length >= 2
                && "CompanyRepresentative".equals(row[0])
                && userID.equals(row[1])) {
                // Ensure row has at least 4 columns
                if (row.length < 5) {
                    String[] expanded = new String[5];
                    for (int j = 0; j < row.length; j++) expanded[j] = row[j];
                    for (int j = row.length; j < 5; j++) expanded[j] = "";
                    row = expanded;
                }
                // store status in the 5th column (index 4)
                row[4] = status;
                loginList.set(i, row);
                updated = true;
                break;
            }
        }

        // Persist changes back to CSV if updated
        if (updated) {
            String csvFile = "Code/Backend/Lib/login_list.csv";
            File inputFile = new File(csvFile);
            File tempFile = new File("Code/Backend/Lib/login_list.tmp");

            try (FileWriter writer = new FileWriter(tempFile)) {
                // Write header (with status column)
                writer.append("identity,userID,passwordHash,salt,status\n");
                // Write rows
                for (String[] data : loginList) {
                    // Normalize to 5 columns when writing
                    String[] out = new String[] {"", "", "", "", ""};
                    for (int k = 0; k < data.length && k < 5; k++) {
                        out[k] = (data[k] == null) ? "" : data[k];
                    }
                    // Escape all fields when writing
                    writer.append(String.join(",",
                        ControlUtils.escapeCsvField(out[0]),
                        ControlUtils.escapeCsvField(out[1]),
                        ControlUtils.escapeCsvField(out[2]),
                        ControlUtils.escapeCsvField(out[3]),
                        ControlUtils.escapeCsvField(out[4]))).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // Replace original with temp
            if (inputFile.exists()) inputFile.delete();
            tempFile.renameTo(inputFile);

            // Reload in-memory state
            loadLoginListFromDB();
        } else {
            throw new IllegalArgumentException("UserLoginDirectoryControl.updateCompanyRepStatusInLogin(): CompanyRepresentative not found for userID=" + userID);
        }
    }
    private void updateCompanyRepStatusInCompanyRepCSV(String userID, String status) {
    String csvFile = "Code/Backend/Lib/company_representative.csv";
        File inputFile = new File(csvFile);
        File tempFile = new File("Code/Backend/Lib/company_representative.tmp");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {

            String header = reader.readLine();
            // ensure header present and write normalized header with 7 columns
            if (header == null || header.trim().isEmpty()) {
                writer.append("userID,name,email,position,accountStatus,companyName,department").append("\n");
            } else {
                writer.append(header).append("\n");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = ControlUtils.splitCsvLine(line);
                // Unescape fields
                for (int i = 0; i < data.length; i++) {
                    data[i] = ControlUtils.unescapeCsvField(data[i]);
                }
                // normalize to 7 columns
                if (data.length < 7) {
                    String[] expanded = new String[7];
                    for (int j = 0; j < data.length; j++) expanded[j] = data[j];
                    for (int j = data.length; j < 7; j++) expanded[j] = "";
                    data = expanded;
                }
                if (data.length > 0 && data[0].equals(userID)) {
                    // Update status (5th column, index 4)
                    data[4] = status;
                    updated = true;
                }
                // Escape all fields when writing back
                writer.append(String.join(",",
                    ControlUtils.escapeCsvField(data[0]),
                    ControlUtils.escapeCsvField(data[1]),
                    ControlUtils.escapeCsvField(data[2]),
                    ControlUtils.escapeCsvField(data[3]),
                    ControlUtils.escapeCsvField(data[4]),
                    ControlUtils.escapeCsvField(data[5]),
                    ControlUtils.escapeCsvField(data[6]))).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (updated) {
            inputFile.delete();
            tempFile.renameTo(inputFile);
        } else {
            tempFile.delete();
            throw new IllegalArgumentException("UserLoginDirectoryControl.updateCompanyRepStatusInLogin(): CompanyRepresentative not found for userID=" + userID);
        }
    }



    //intialization related methods
    private void checkHaveInitialized() {
        String csvFile = "Code/Backend/Lib/have_initialized.csv";
        File file = new File(csvFile);
        String line = "";
        String csvSplitBy = ",";

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); 
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.append("have_initialized\n");
                    writer.append("false\n");
                }
            }

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                br.readLine(); // Skip header
                if ((line = br.readLine()) != null) {
                    String[] data = line.split(csvSplitBy);
                    haveInitialized = Boolean.parseBoolean(data[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setHaveInitialized(boolean status) {
        String csvFile = "Code/Backend/Lib/have_initialized.csv";
        File inputFile = new File(csvFile);
        File tempFile = new File("Code/Backend/Lib/have_initialized.tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(tempFile)) {

            String header = reader.readLine();
            writer.append(header).append("\n");
            writer.append(Boolean.toString(status)).append("\n");

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
        haveInitialized = status;
    }
    private void loadUsersFromExamplesToDB(){
        checkHaveInitialized();
        if (haveInitialized) {
            return;
        }
        String filePathExampleStaff = "Code/Backend/Lib_example/sample_staff_list.csv";
        String filePathExampleStudent = "Code/Backend/Lib_example/sample_student_list.csv";
        String filePathExampleCompanyRep = "Code/Backend/Lib_example/sample_company_representative_list.csv";
    String filePathDBLogin = "Code/Backend/Lib/login_list.csv";
        String filePathDBStaffString = "Code/Backend/Lib/staff.csv";
        String filePathDBStudentString = "Code/Backend/Lib/student.csv";
        
        BufferedReader br = null;
        String line;
        try {
            // Ensure DB CSVs exist with headers and end with newline to avoid concatenation
            ControlUtils.ensureCsvPrepared(filePathDBLogin, "identity,userID,passwordHash,salt,status");
            ControlUtils.ensureCsvPrepared(filePathDBStaffString, "userID,name,email,department,role");
            ControlUtils.ensureCsvPrepared(filePathDBStudentString, "userID,name,email,major,year,hasAcceptedInternshipOpportunity");
            ControlUtils.ensureCsvPrepared("Code/Backend/Lib/company_representative.csv", "userID,name,email,position,accountStatus,companyName,department");
            // Load Staff - map sample to DB header: userID,name,email,department,role
            br = new BufferedReader(new FileReader(filePathExampleStaff));
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String staffID = values.length > 0 ? values[0] : "";
                String staffName = values.length > 1 ? values[1] : "";
                String staffRole = values.length > 2 ? values[2] : "";
                String staffDept = values.length > 3 ? values[3] : "";
                String staffEmail = values.length > 4 ? values[4] : "";
                try (FileWriter writer = new FileWriter(filePathDBStaffString, true)) {
                    // Escape all fields when writing to DB
                    writer.append(String.join(",", 
                        ControlUtils.escapeCsvField(staffID),
                        ControlUtils.escapeCsvField(staffName),
                        ControlUtils.escapeCsvField(staffEmail),
                        ControlUtils.escapeCsvField(staffDept),
                        ControlUtils.escapeCsvField(staffRole)));
                    writer.append("\n");
                }
                try (FileWriter writer = new FileWriter(filePathDBLogin, true)) {
                    String salt = generateSalt();
                    // Staff have no status column value - escape all fields
                    writer.append(String.join(",", 
                        ControlUtils.escapeCsvField("Staff"),
                        ControlUtils.escapeCsvField(staffID),
                        ControlUtils.escapeCsvField(hashPassword("password", salt)),
                        ControlUtils.escapeCsvField(salt),
                        ControlUtils.escapeCsvField("")));
                    writer.append("\n");
                }
            }
            br.close();

            // Load Students - map sample (StudentID,Name,Major,Year,Email) to DB header: userID,name,email,major,year,hasAcceptedInternshipOpportunity
            br = new BufferedReader(new FileReader(filePathExampleStudent));
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String studentID = values.length > 0 ? values[0] : "";
                String studentName = values.length > 1 ? values[1] : "";
                String studentMajor = values.length > 2 ? values[2] : "";
                String studentYear = values.length > 3 ? values[3] : "";
                String studentEmail = values.length > 4 ? values[4] : "";
                try (FileWriter writer = new FileWriter(filePathDBStudentString, true)) {
                    // Escape all fields when writing to DB
                    writer.append(String.join(",",
                        ControlUtils.escapeCsvField(studentID),
                        ControlUtils.escapeCsvField(studentName),
                        ControlUtils.escapeCsvField(studentEmail),
                        ControlUtils.escapeCsvField(studentMajor),
                        ControlUtils.escapeCsvField(studentYear),
                        ControlUtils.escapeCsvField("false")));
                    writer.append("\n");
                }
                try (FileWriter writer = new FileWriter(filePathDBLogin, true)) {
                    String salt = generateSalt();
                    // Students have no status column value - escape all fields
                    writer.append(String.join(",",
                        ControlUtils.escapeCsvField("Student"),
                        ControlUtils.escapeCsvField(studentID),
                        ControlUtils.escapeCsvField(hashPassword("password", salt)),
                        ControlUtils.escapeCsvField(salt),
                        ControlUtils.escapeCsvField("")));
                    writer.append("\n");
                }
            }
            br.close();

            // Load Company Representatives - map sample to DB header: userID,name,email,position,accountStatus,companyName,department
            br = new BufferedReader(new FileReader(filePathExampleCompanyRep));
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String compID = values.length > 0 ? values[0] : "";
                String compName = values.length > 1 ? values[1] : "";
                String compCompanyName = values.length > 2 ? values[2] : "";
                String compDept = values.length > 3 ? values[3] : "";
                String compPosition = values.length > 4 ? values[4] : "";
                String compEmail = values.length > 5 ? values[5] : "";
                String compStatus = values.length > 6 ? values[6] : "";
                try (FileWriter writer = new FileWriter("Code/Backend/Lib/company_representative.csv", true)) {
                    // Escape all fields when writing to DB
                    writer.append(String.join(",",
                        ControlUtils.escapeCsvField(compID),
                        ControlUtils.escapeCsvField(compName),
                        ControlUtils.escapeCsvField(compEmail),
                        ControlUtils.escapeCsvField(compPosition),
                        ControlUtils.escapeCsvField(compStatus),
                        ControlUtils.escapeCsvField(compCompanyName),
                        ControlUtils.escapeCsvField(compDept)));
                    writer.append("\n");
                }
                try (FileWriter writer = new FileWriter(filePathDBLogin, true)) {
                    String salt = generateSalt();
                    // Preserve the status from the example data - escape all fields
                    writer.append(String.join(",",
                        ControlUtils.escapeCsvField("CompanyRepresentative"),
                        ControlUtils.escapeCsvField(compID),
                        ControlUtils.escapeCsvField(hashPassword("password", salt)),
                        ControlUtils.escapeCsvField(salt),
                        ControlUtils.escapeCsvField(compStatus)));
                    writer.append("\n");
                }
            }
            br.close();

            setHaveInitialized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads user data from sample CSV files and initializes the database.
     * <p>
     * This method reads from unquoted sample CSV files in Lib_example/ and writes
     * properly escaped CSV data to the database files in Lib/. All text fields
     * are escaped to handle special characters (commas, quotes) that may appear
     * in names, company names, departments, positions, etc.
     * </p>
     * <p>
     * Sample file format (simple CSV): field1,field2,field3<br>
     * Database file format (escaped CSV): "field1","field2","field3"
     * </p>
     * 
     * @implNote Only runs once - checks have_initialized.csv flag before execution
     */
}


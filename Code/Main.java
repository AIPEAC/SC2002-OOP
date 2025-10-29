
import java.util.Scanner;
import Boundary.*;
import Interface.*;
import Entity.*;
import Entity.User.*;


public class Main {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        System.out.println("Please choose domain to login: 1. CareerStaff 2. Student 3. CompanyRepresentative");
        String domain=sc.next();
        System.out.println("enter userID");
        String userID=sc.next();
        System.out.println("enter password");
        String password=sc.next();
        domainChoose: while (true) {
            switch (domain) {
                case "1":
                    CareerStaffCLI careerStaffCLI = new CareerStaffCLI();
                    careerStaffCLI.login(userID, password);
                    break domainChoose;
                case "2":
                    StudentCLI studentCLI = new StudentCLI();
                    studentCLI.login(userID, password);
                    break domainChoose;
                case "3":
                    CompanyRepresentativeCLI companyRepCLI = new CompanyRepresentativeCLI();
                    companyRepCLI.login(userID, password);
                    break domainChoose;
                default:
                    System.out.println("Invalid domain. Please choose again: 1. CareerStaff 2. Student 3. CompanyRepresentative");
                    domain = sc.next();
            }
            
        }
        
    }
}
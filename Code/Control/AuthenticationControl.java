package Control;
import Entity.Users.*;

public class AuthenticationControl {
    private User currentUser=null;
    private String companyName=null;

    public AuthenticationControl(){
        this.currentUser=null;
    }
    public AuthenticationControl(User user){
        currentUser=user;
    }

    protected void setLoggedin(User user){
        currentUser=user;
    }
    public boolean isLoggedIn(){
        if (currentUser==null){
            return false;
        }else{
            return true;
        }
    }    
    protected User getUser(){
        return currentUser;
    }
    public String getUserIdentity(){
        
        if (currentUser==null){
            throw new IllegalStateException("Not logged in: cannot get user identity.");
        } else if (currentUser instanceof CareerStaff){
            return "CareerStaff";
        } else if (currentUser instanceof Student){
            return "Student";
        } else if (currentUser instanceof CompanyRepresentative){
            return "CompanyRepresentative";
        } else {
            throw new IllegalArgumentException("Unknown user type for current user.");
        }
        
        
    }

    public String getUserID(){
        return currentUser.getUserID();
    }
    public void setCompanyName(String companyName){
        this.companyName=companyName;
    }
    public String getCompanyName(){
        return this.companyName;
    }
}

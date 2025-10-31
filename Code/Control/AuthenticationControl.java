package Control;
import Entity.Users.*;

public class AuthenticationControl {
    private User currentUser=null;

    public AuthenticationControl(){
        this.currentUser=null;
    }
    public AuthenticationControl(User user){
        currentUser=user;
    }

    public void setLoggedin(User user){
        currentUser=user;
    }
    public boolean isLoggedIn(){
        if (currentUser==null){
            return false;
        }else{
            return true;
        }
    }    
    public User getUser(){
        return currentUser;
    }
    public String getUserIdentity(){
        
        try {
            if (currentUser==null){
                throw new Exception("bug: Authentication.java : getUserIdentity() Not Supposed to access getUserIdentity without a valid login");
            }else if (currentUser instanceof CareerStaff){
                return "CareerStaff";
            }else if (currentUser instanceof Student){
                return "Student";
            }else if (currentUser instanceof CompanyRepresentative){
                return "CompanyRepresentative";
            }else{
                throw new Exception("bug: Authentication.java : getUserIdentity() found that a User exist but ");
            }
            
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
        
        
    }

    public String getUserID(){
        return currentUser.getUserID();
    }
}

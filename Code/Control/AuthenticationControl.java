package Control;
import Entity.Users.*;

public class AuthenticationControl {
    private User currentUser=null;
    public AuthenticationControl(){

    }
    public void setLoggedin(User user){
        currentUser=user;
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
}

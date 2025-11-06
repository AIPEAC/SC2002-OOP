package Entity.Users;



public abstract class User {
    public enum Role { STUDENT, COMPANY_REP, CAREER_STAFF }

    private String userID;
    private String name;
    private String email;

    public User(String userID, String name, String email) {
        this.userID = userID;
        this.name = name;
        this.email = email;
    }

    public String getUserID() { 
        return userID; 
    }
    public String getName() { 
        return name; 
    }
    public String getEmail() { 
        return email; 
    }
}





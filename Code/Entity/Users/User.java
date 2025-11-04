package Entity.Users;



public abstract class User {
    public enum Role { STUDENT, COMPANY_REP, CAREER_STAFF }

    private String userID;
    private String name;
    private String email;
    private Role role;

    public User(String userID, String name, String email, Role role) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.role = role;
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
    public Role getRole() { 
        return role; 
    }
}





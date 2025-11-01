package Entity.Users;



public abstract class User {
    private String userID;
    private String name;
    private String email;

    public User(String userID, String name, String email) {
        this.userID = userID;
        this.name = name;
        this.email = email;
    }

    // Getters for public info
    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    
}





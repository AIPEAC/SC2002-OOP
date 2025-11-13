package Control;

/**
 * Central initialization and management class for all backend controllers.
 * This class encapsulates the creation and dependency injection of all control layer components,
 * preventing direct manipulation by the frontend layer and ensuring proper initialization order.
 * <p>
 * The frontend should only interact with this class to obtain controller instances,
 * rather than instantiating controllers directly. This enforces separation of concerns
 * and maintains the integrity of the backend architecture.
 * </p>
 * 
 * @author Allen
 * @version 1.0
 */
public class ControlInitializer {
    /** Authentication controller for user login and session management */
    private final AuthenticationControl authCtrl;
    
    /** User login directory controller for credential management */
    private final UserLoginDirectoryControl userLoginDirCtrl;
    
    /** Login controller for login operations */
    private final LoginControl loginCtrl;
    
    /** Internship controller for internship opportunity management */
    private final InternshipControl intCtrl;
    
    /** Application controller for student application management */
    private final ApplicationControl appCtrl;
    
    /** Report controller for generating reports */
    private final ReportControl reportCtrl;
    
    /** User controller for user account operations */
    private final UserControl userCtrl;
    
    /**
     * Constructs and initializes all backend controllers in the correct order.
     * Sets up all dependencies and cross-references between controllers.
     * This constructor ensures that all controllers are properly initialized
     * and wired together before being exposed to the frontend.
     */
    public ControlInitializer() {
        // Initialize controllers in dependency order
        authCtrl = new AuthenticationControl();
        userLoginDirCtrl = new UserLoginDirectoryControl(authCtrl);
        loginCtrl = new LoginControl(authCtrl, userLoginDirCtrl);
        intCtrl = new InternshipControl(authCtrl);
        appCtrl = new ApplicationControl(authCtrl, intCtrl);
        
        // Set up bidirectional dependencies
        intCtrl.setApplicationControl(appCtrl);
        
        // Initialize remaining controllers
        reportCtrl = new ReportControl(authCtrl, intCtrl);
        userCtrl = new UserControl(userLoginDirCtrl, authCtrl);
    }
    
    /**
     * Gets the authentication controller.
     * 
     * @return the authentication controller instance
     */
    public AuthenticationControl getAuthenticationControl() {
        return authCtrl;
    }
    
    /**
     * Gets the user login directory controller.
     * 
     * @return the user login directory controller instance
     */
    public UserLoginDirectoryControl getUserLoginDirectoryControl() {
        return userLoginDirCtrl;
    }
    
    /**
     * Gets the login controller.
     * 
     * @return the login controller instance
     */
    public LoginControl getLoginControl() {
        return loginCtrl;
    }
    
    /**
     * Gets the internship controller.
     * 
     * @return the internship controller instance
     */
    public InternshipControl getInternshipControl() {
        return intCtrl;
    }
    
    /**
     * Gets the application controller.
     * 
     * @return the application controller instance
     */
    public ApplicationControl getApplicationControl() {
        return appCtrl;
    }
    
    /**
     * Gets the report controller.
     * 
     * @return the report controller instance
     */
    public ReportControl getReportControl() {
        return reportCtrl;
    }
    
    /**
     * Gets the user controller.
     * 
     * @return the user controller instance
     */
    public UserControl getUserControl() {
        return userCtrl;
    }
}

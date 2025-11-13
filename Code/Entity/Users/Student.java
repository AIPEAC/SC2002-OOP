package Entity.Users;

import java.util.List;

/**
 * Represents a student user in the Internship Placement Management System.
 * Students can view and apply for internship opportunities based on their year of study and major.
 * Year 1-2 students can only apply for Basic level internships, while Year 3-4 can apply for any level.
 * Students can accept at most one internship placement.
 * 
 * @author Allen
 * @version 1.0
 */
public class Student extends User {
    /** List of majors the student is enrolled in (e.g., CSC, EEE, MAE) */
    private List<String> majors;
    
    /** Year of study (1 to 4) */
    private int year;
    
    /** Flag indicating whether the student has accepted an internship opportunity */
    private boolean hasAcceptedInternshipOpportunity = false;

    /**
     * Constructs a Student with the specified details.
     * 
     * @param userID the unique student ID (format: U followed by 7 digits and a letter, e.g., U2345123F)
     * @param name the full name of the student
     * @param email the student's email address
     * @param majors the list of major codes the student is enrolled in
     * @param year the current year of study (1-4)
     * @param hasAcceptedInternshipOpportunity whether the student has already accepted an internship
     */
    public Student(String userID, String name, String email, List<String> majors, int year, boolean hasAcceptedInternshipOpportunity) {
        super(userID, name, email);
        // Additional fields for Student
        this.majors = majors;
        this.year = year;
        this.hasAcceptedInternshipOpportunity = hasAcceptedInternshipOpportunity;
    }

    /**
     * Sets the acceptance status to true when student accepts an internship offer.
     */
    public void setAcceptanceStatusToTrue() {
        hasAcceptedInternshipOpportunity = true;
    }

    /**
     * Sets the acceptance status to false (e.g., when internship is cancelled or withdrawn).
     */
    public void setAcceptanceStatusToFalse() {
        hasAcceptedInternshipOpportunity = false;
    }

    /**
     * Checks whether the student has accepted an internship opportunity.
     * 
     * @return true if student has accepted an internship, false otherwise
     */
    public boolean getAcceptanceStatus() {
        return hasAcceptedInternshipOpportunity;
    }

    /**
     * Gets the list of majors the student is enrolled in.
     * 
     * @return a list of major codes (e.g., ["CSC", "EEE"])
     */
    public List<String> getMajors() {
        return majors;
    }

    /**
     * Gets the student's current year of study.
     * 
     * @return the year (1-4)
     */
    public int getYear() {
        return year;
    }

}

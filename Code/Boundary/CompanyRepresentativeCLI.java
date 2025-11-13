package Boundary;

import javax.swing.*;

import Boundary.Helper.UIHelper;
import Control.*;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Boundary class for company representative user interface.
 * Allows company representatives to create internship opportunities (up to 5),
 * view their opportunities' status, manage applications (approve/reject),
 * view detailed student information, and toggle opportunity visibility.
 * Each opportunity can have maximum 10 slots.
 * 
 * @author Allen
 * @version 1.0
 */
public class CompanyRepresentativeCLI extends AbstractCLI {
    /** Main frame for company representative interface */
    private JFrame frame;

    /**
     * Constructs a CompanyRepresentativeCLI.
     * 
     * @param intCtrl the internship controller
     * @param loginCtrl the login controller for password changes
     */
    public CompanyRepresentativeCLI(InternshipControl intCtrl, LoginControl loginCtrl) {
        super(intCtrl);
        setLoginControl(loginCtrl);
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Company Representative");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(0,1,5,5));

            JButton chPwd = new JButton("Change Password");
            chPwd.addActionListener(e -> changePassword());
            p.add(chPwd);

            JButton viewOpp = new JButton("View Internship Opportunities");
            viewOpp.addActionListener(e -> viewFilteredInternshipOpportunities());
            p.add(viewOpp);

            JButton createOpp = new JButton("Create Internship Opportunity");
            createOpp.addActionListener(e -> createInternshipOpportunity());
            p.add(createOpp);

            JButton checkStatus = new JButton("Check My Internship Opportunities' Status");
            checkStatus.addActionListener(e -> checkMyInternshipOppStatus());
            p.add(checkStatus);

            JButton logout = new JButton("Logout");
            logout.addActionListener(e -> {
                UIHelper.closeLoggedInPopup();
                frame.dispose();
            });
            p.add(logout);

            frame.setContentPane(p);
            frame.setVisible(true);
        });
    }

    // changePassword and viewFilteredInternshipOpportunities inherited from AbstractCLI



    private void createInternshipOpportunity() {
        JTextField title = new JTextField();
        JTextArea desc = new JTextArea(5,30);
        String[] levels = {"Basic","Intermediate","Advanced"};
        JComboBox<String> level = new JComboBox<>(levels);
        JTextField open = new JTextField();
        JTextField close = new JTextField();
        JTextField slots = new JTextField("1");

        List<String> allMajors = new ArrayList<>();
        try {
            allMajors = intCtrl.getAvailableMajors();
        } catch (Exception ex) {
            // fallback to empty list
        }
        List<String> selectedMajors = UIHelper.showMultiSelectMajors(allMajors);

        JPanel panel = new JPanel(new BorderLayout(5,5));
        JPanel top = new JPanel(new GridLayout(0,2));
        top.add(new JLabel("Title:")); top.add(title);
        top.add(new JLabel("Level:")); top.add(level);
        top.add(new JLabel("Open Date (yyyy-MM-dd):")); top.add(open);
        top.add(new JLabel("Close Date (yyyy-MM-dd):")); top.add(close);
        top.add(new JLabel("Slots:")); top.add(slots);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JLabel("Description:"), BorderLayout.CENTER);
        panel.add(new JScrollPane(desc), BorderLayout.SOUTH);

        int res = JOptionPane.showConfirmDialog(frame, panel, "Create Internship", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String id = intCtrl.requestCreateInternshipOpportunity(title.getText(), desc.getText(), (String)level.getSelectedItem(), selectedMajors, open.getText(), close.getText(), slots.getText());
                JOptionPane.showMessageDialog(frame, "Created internship with ID: " + id);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void checkMyInternshipOppStatus() {
        try {
            List<String> lines = intCtrl.getMyInternshipsWithStatus();
            if (lines.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No internship opportunities found for this company representative.");
                return;
            }
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            // Header row
            JPanel header = new JPanel(new GridBagLayout());
            header.setBackground(new Color(240,240,240));
            GridBagConstraints hgb = new GridBagConstraints();
            hgb.insets = new Insets(4,6,4,6);
            hgb.gridy = 0;
            hgb.gridx = 0; hgb.weightx = 0; hgb.fill = GridBagConstraints.NONE; header.add(new JLabel("Actions", SwingConstants.CENTER), hgb);
            hgb.gridx = 1; hgb.weightx = 0.15; hgb.fill = GridBagConstraints.HORIZONTAL; header.add(new JLabel("ID", SwingConstants.CENTER), hgb);
            hgb.gridx = 2; hgb.weightx = 0.25; header.add(new JLabel("Title", SwingConstants.CENTER), hgb);
            hgb.gridx = 3; hgb.weightx = 0.1; header.add(new JLabel("Level", SwingConstants.CENTER), hgb);
            hgb.gridx = 4; hgb.weightx = 0.15; header.add(new JLabel("Company", SwingConstants.CENTER), hgb);
            hgb.gridx = 5; hgb.weightx = 0.2; header.add(new JLabel("Preferred Majors", SwingConstants.CENTER), hgb);
            hgb.gridx = 6; hgb.weightx = 0.15; header.add(new JLabel("Status", SwingConstants.CENTER), hgb);
            header.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.GRAY));
            listPanel.add(header);

            for (String line : lines) {
                String id = parseFieldValue(line, "internshipID");
                String title = parseFieldValue(line, "internshipTitle");
                String level = parseFieldValue(line, "internshipLevel");
                String company = parseFieldValue(line, "companyName");
                String majorsRaw = parseFieldValue(line, "preferredMajors");
                String majors = formatMajorsSimple(majorsRaw);
                String status = parseFieldValue(line, "status");

                JPanel row = new JPanel(new GridBagLayout());
                GridBagConstraints r = new GridBagConstraints();
                r.insets = new Insets(6,6,6,6);
                r.gridy = 0;

                // Actions column
                r.gridx = 0; r.weightx = 0; r.fill = GridBagConstraints.NONE;
                JButton checkApps = new JButton("Check Applications");
                String finalId = id;
                String finalStatus = status;
                checkApps.addActionListener(e -> showApplicationsForInternship(finalId));
                // Only enable if approved and has applications
                checkApps.setEnabled("approved".equalsIgnoreCase(finalStatus));
                
                JButton detailsBtn = new JButton("Details");
                detailsBtn.addActionListener(e -> showInternshipDetailsWithActions(finalId, finalStatus));
                
                JButton toggleVis = new JButton("Toggle Visibility");
                toggleVis.addActionListener(e -> {
                    try {
                        boolean newVisibility = intCtrl.changeVisibilityByID(finalId);
                        String visibilityStatus = newVisibility ? "VISIBLE" : "HIDDEN";
                        JOptionPane.showMessageDialog(frame, 
                            "Visibility toggled for: " + finalId + "\nNew status: " + visibilityStatus,
                            "Visibility Updated",
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                actionsPanel.add(checkApps);
                actionsPanel.add(detailsBtn);
                actionsPanel.add(toggleVis);
                actionsPanel.setPreferredSize(new Dimension(400, 28));
                row.add(actionsPanel, r);

                r.gridx = 1; r.weightx = 0.15; r.fill = GridBagConstraints.HORIZONTAL; row.add(new JLabel(id != null ? id : ""), r);
                r.gridx = 2; r.weightx = 0.25; row.add(new JLabel(title != null ? title : ""), r);
                r.gridx = 3; r.weightx = 0.1; row.add(new JLabel(level != null ? level : ""), r);
                r.gridx = 4; r.weightx = 0.15; row.add(new JLabel(company != null ? company : ""), r);
                r.gridx = 5; r.weightx = 0.2; row.add(new JLabel(majors), r);
                r.gridx = 6; r.weightx = 0.15; row.add(new JLabel(status != null ? status : ""), r);
                row.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY));
                listPanel.add(row);
            }

            JScrollPane sp = new JScrollPane(listPanel);
            sp.setPreferredSize(new Dimension(1000, 400));
            JOptionPane.showMessageDialog(frame, sp, "My Internships", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showApplicationsForInternship(String internshipID) {
        try {
            List<String> lines = intCtrl.getApplicationsForInternship(internshipID);
            if (lines.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No pending applications for this internship.");
                return;
            }
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            for (String line : lines) {
                String appNumStr = parseFieldValue(line, "applicationNumber");
                String majors = parseFieldValue(line, "studentMajors");
                String appStatus = parseFieldValue(line, "status");

                JPanel row = new JPanel(new BorderLayout(8,8));
                String rowText = "Application #" + appNumStr + " | Student Majors: " + majors + " | Status: " + appStatus;
                JLabel label = new JLabel(rowText);
                row.add(label, BorderLayout.CENTER);

                JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton viewDetails = new JButton("View Student Details");
                JButton approve = new JButton("Approve");
                JButton reject = new JButton("Reject");

                // Disable if already approved/rejected
                boolean isPending = "pending".equalsIgnoreCase(appStatus);
                approve.setEnabled(isPending);
                reject.setEnabled(isPending);

                String finalAppNum = appNumStr;
                
                viewDetails.addActionListener(e -> {
                    try {
                        int num = Integer.parseInt(finalAppNum);
                        String details = intCtrl.getDetailedStudentInfoForApplication(num);
                        
                        // Parse the details
                        String studentID = parseFieldValue(details, "studentID");
                        String studentName = parseFieldValue(details, "studentName");
                        String studentEmail = parseFieldValue(details, "studentEmail");
                        String studentMajors = parseFieldValue(details, "studentMajors");
                        String studentYear = parseFieldValue(details, "studentYear");
                        String status = parseFieldValue(details, "status");
                        
                        // Display in a formatted panel
                        JPanel detailPanel = new JPanel(new GridLayout(0, 2, 10, 5));
                        detailPanel.add(new JLabel("Student ID:"));
                        detailPanel.add(new JLabel(studentID));
                        detailPanel.add(new JLabel("Name:"));
                        detailPanel.add(new JLabel(studentName));
                        detailPanel.add(new JLabel("Email:"));
                        detailPanel.add(new JLabel(studentEmail));
                        detailPanel.add(new JLabel("Majors:"));
                        detailPanel.add(new JLabel(studentMajors));
                        detailPanel.add(new JLabel("Year:"));
                        detailPanel.add(new JLabel(studentYear));
                        detailPanel.add(new JLabel("Application Status:"));
                        detailPanel.add(new JLabel(status));
                        
                        JOptionPane.showMessageDialog(frame, detailPanel, 
                            "Student Details - Application #" + finalAppNum, 
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                approve.addActionListener(e -> {
                    try {
                        int num = Integer.parseInt(finalAppNum);
                        String message = intCtrl.approveApplicationForInternship(internshipID, num);
                        // Check if message contains "FULL" notification
                        if (message != null && message.contains("FULL")) {
                            JOptionPane.showMessageDialog(frame, message, "Application Approved", JOptionPane.WARNING_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(frame, message != null ? message : "Application approved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                        approve.setEnabled(false); reject.setEnabled(false);
                    } catch (Exception ex) {
                        ex.printStackTrace(); // Print full stack trace for debugging
                        String errorMsg = ex.getMessage();
                        if (errorMsg == null || errorMsg.trim().isEmpty()) {
                            errorMsg = "An error occurred: " + ex.getClass().getSimpleName();
                        }
                        JOptionPane.showMessageDialog(frame, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                reject.addActionListener(e -> {
                    try {
                        int num = Integer.parseInt(finalAppNum);
                        intCtrl.rejectApplicationForInternship(internshipID, num);
                        JOptionPane.showMessageDialog(frame, "Rejected application: " + num);
                        approve.setEnabled(false); reject.setEnabled(false);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                btns.add(viewDetails);
                btns.add(approve);
                btns.add(reject);
                row.add(btns, BorderLayout.EAST);
                row.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY));
                listPanel.add(row);
            }

            JScrollPane sp = new JScrollPane(listPanel);
            sp.setPreferredSize(new Dimension(700, Math.min(400, lines.size()*60 + 40)));
            JOptionPane.showMessageDialog(frame, sp, "Applications for " + internshipID, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String parseFieldValue(String line, String key) {
        String marker = key + "=";
        int idx = line.indexOf(marker);
        if (idx < 0) return null;
        int start = idx + marker.length();
        String DELIM = " | ";
        int end = line.indexOf(DELIM, start);
        if (end < 0) end = line.length();
        return line.substring(start, end).trim();
    }

    private String formatMajorsSimple(String raw) {
        if (raw == null) return "[]";
        String r = raw.trim();
        if (r.isEmpty()) return "[]";
        while (r.startsWith("[[") && r.endsWith("]]")) {
            r = r.substring(1, r.length()-1).trim();
        }
        if (r.startsWith("[") && r.endsWith("]")) {
            return r;
        }
        return "[" + r + "]";
    }
    
    /**
     * Shows internship details in a dialog with Edit and Delete buttons available.
     * Edit and Delete buttons are only enabled if the status is "pending".
     * 
     * @param internshipID the internship to view
     * @param status the current status (pending, approved, etc.)
     */
    private void showInternshipDetailsWithActions(String internshipID, String status) {
        try {
            JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Get internship details from controller
            List<String> details = intCtrl.getInternshipDetails(internshipID);
            if (details == null || details.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Internship details not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Display all internship information from the list
            // Expected format: [internshipID, title, description, level, majors, openDate, closeDate, company, slots, pendingApps, acceptedApps, visibility]
            detailsPanel.add(new JLabel("Internship ID:"));
            detailsPanel.add(new JLabel(internshipID));
            
            String title = details.size() > 1 ? details.get(1) : "";
            detailsPanel.add(new JLabel("Title:"));
            detailsPanel.add(new JLabel(title));
            
            String desc = details.size() > 2 ? details.get(2) : "";
            detailsPanel.add(new JLabel("Description:"));
            // Use JLabel for description to make it completely non-editable
            JLabel descLabel = new JLabel("<html>" + desc.replace("\n", "<br>") + "</html>");
            detailsPanel.add(descLabel);
            
            String level = details.size() > 3 ? details.get(3) : "";
            detailsPanel.add(new JLabel("Level:"));
            detailsPanel.add(new JLabel(level));
            
            String majors = details.size() > 4 ? details.get(4) : "";
            detailsPanel.add(new JLabel("Preferred Majors:"));
            detailsPanel.add(new JLabel(majors));
            
            String company = details.size() > 7 ? details.get(7) : "";
            detailsPanel.add(new JLabel("Company:"));
            detailsPanel.add(new JLabel(company));
            
            String openDate = details.size() > 5 ? details.get(5) : "";
            detailsPanel.add(new JLabel("Opening Date:"));
            detailsPanel.add(new JLabel(openDate));
            
            String closeDate = details.size() > 6 ? details.get(6) : "";
            detailsPanel.add(new JLabel("Closing Date:"));
            detailsPanel.add(new JLabel(closeDate));
            
            String slots = details.size() > 8 ? details.get(8) : "";
            detailsPanel.add(new JLabel("Number of Slots:"));
            detailsPanel.add(new JLabel(slots));
            
            detailsPanel.add(new JLabel("Status:"));
            detailsPanel.add(new JLabel(status));
            
            // Add Edit and Delete buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            
            JButton editBtn = new JButton("Edit");
            editBtn.addActionListener(e -> {
                editInternshipOpportunity(internshipID);
            });
            editBtn.setEnabled("pending".equalsIgnoreCase(status));
            
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.addActionListener(e -> {
                deleteInternshipOpportunity(internshipID);
            });
            deleteBtn.setEnabled("pending".equalsIgnoreCase(status));
            
            buttonPanel.add(editBtn);
            buttonPanel.add(deleteBtn);
            
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(detailsPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            JOptionPane.showMessageDialog(frame, mainPanel, "Internship Details - " + internshipID, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error loading details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Opens edit dialog for internship opportunity (only allowed if pending status).
     * Allows company rep to modify title, description, level, dates, slots, and majors.
     * 
     * @param internshipID the internship to edit
     */
    private void editInternshipOpportunity(String internshipID) {
        try {
            // Get current internship details from control
            List<String> details = intCtrl.getInternshipDetails(internshipID);
            if (details == null || details.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Internship details not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse current values from details list (format has "Label: Value" prefixes)
            String currentTitle = extractValue(details, "Title: ");
            String currentDesc = extractValue(details, "Description: ");
            String currentLevel = extractValue(details, "Level: ");
            String currentMajorsStr = extractValue(details, "Preferred Majors: ");
            String currentOpenDate = extractDateOnly(extractValue(details, "Opening Date: "));
            String currentCloseDate = extractDateOnly(extractValue(details, "Closing Date: "));
            String currentSlots = extractValue(details, "Slots: ");
            
            // Parse current majors from string format (e.g., "[CSC, EEE, MAE]")
            List<String> currentSelectedMajors = new ArrayList<>();
            if (currentMajorsStr != null && !currentMajorsStr.isEmpty() && !"N/A".equals(currentMajorsStr)) {
                String majorsClean = currentMajorsStr.replaceAll("[\\[\\]]", "").trim();
                if (!majorsClean.isEmpty()) {
                    for (String m : majorsClean.split(",")) {
                        currentSelectedMajors.add(m.trim());
                    }
                }
            }
            
            // Create edit panel
            JPanel editPanel = new JPanel(new BorderLayout(5, 5));
            editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Top panel with title, level, dates, slots
            JPanel top = new JPanel(new GridLayout(0, 2, 10, 10));
            
            // Title
            top.add(new JLabel("Title:"));
            JTextField titleField = new JTextField(currentTitle, 20);
            top.add(titleField);
            
            // Level
            top.add(new JLabel("Level:"));
            JComboBox<String> levelBox = new JComboBox<>(new String[]{"Basic", "Intermediate", "Advanced"});
            levelBox.setSelectedItem(currentLevel);
            top.add(levelBox);
            
            // Open Date
            top.add(new JLabel("Open Date (yyyy-MM-dd):"));
            JTextField openDateField = new JTextField(currentOpenDate, 20);
            top.add(openDateField);
            
            // Close Date
            top.add(new JLabel("Close Date (yyyy-MM-dd):"));
            JTextField closeDateField = new JTextField(currentCloseDate, 20);
            top.add(closeDateField);
            
            // Number of Slots
            top.add(new JLabel("Number of Slots (1-10):"));
            JTextField slotsField = new JTextField(currentSlots, 20);
            top.add(slotsField);
            
            editPanel.add(top, BorderLayout.NORTH);
            
            // Middle panel with majors
            JPanel majorsPanel = new JPanel(new BorderLayout());
            majorsPanel.setBorder(BorderFactory.createTitledBorder("Preferred Majors"));
            
            final List<String> allMajors = new ArrayList<>();
            try {
                allMajors.addAll(intCtrl.getAvailableMajors());
            } catch (Exception ex) {
                // fallback to empty list
            }
            // Create a simple list showing current majors (could be enhanced with checkboxes)
            JTextArea majorsArea = new JTextArea(3, 30);
            majorsArea.setText(String.join(", ", currentSelectedMajors));
            majorsArea.setLineWrap(true);
            majorsArea.setWrapStyleWord(true);
            JLabel majorsHint = new JLabel("<html>Current majors: " + String.join(", ", currentSelectedMajors) + 
                                          "<br>To change majors, click 'Select Majors' button below</html>");
            majorsPanel.add(majorsHint, BorderLayout.CENTER);
            editPanel.add(majorsPanel, BorderLayout.CENTER);
            
            // Bottom panel with description
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
            JTextArea descField = new JTextArea(currentDesc, 5, 30);
            descField.setLineWrap(true);
            descField.setWrapStyleWord(true);
            bottomPanel.add(new JScrollPane(descField), BorderLayout.CENTER);
            editPanel.add(bottomPanel, BorderLayout.SOUTH);
            
            // Create wrapper panel with majors selector button
            JPanel wrapperPanel = new JPanel(new BorderLayout());
            wrapperPanel.add(editPanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton selectMajorsBtn = new JButton("Select Majors");
            final List<String> selectedMajors = new ArrayList<>(currentSelectedMajors);
            selectMajorsBtn.addActionListener(e -> {
                List<String> newMajors = UIHelper.showMultiSelectMajors(allMajors);
                if (newMajors != null) {
                    selectedMajors.clear();
                    selectedMajors.addAll(newMajors);
                    // Update hint
                    majorsHint.setText("<html>Current majors: " + String.join(", ", selectedMajors) + 
                                      "<br>To change majors, click 'Select Majors' button below</html>");
                }
            });
            buttonPanel.add(selectMajorsBtn);
            wrapperPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            int result = JOptionPane.showConfirmDialog(frame, wrapperPanel, "Edit Internship Opportunity", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String title = titleField.getText().trim();
                String description = descField.getText().trim();
                String level = (String) levelBox.getSelectedItem();
                String openDate = openDateField.getText().trim();
                String closeDate = closeDateField.getText().trim();
                String slots = slotsField.getText().trim();
                
                intCtrl.editInternshipOpportunity(internshipID, title, description, level, selectedMajors, openDate, closeDate, slots);
                JOptionPane.showMessageDialog(frame, "Internship opportunity updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                checkMyInternshipOppStatus(); // Refresh the list
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error editing internship: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to extract value from a prefixed string.
     * E.g., "Title: My Title" -> "My Title"
     */
    private String extractValue(List<String> details, String prefix) {
        for (String detail : details) {
            if (detail.startsWith(prefix)) {
                return detail.substring(prefix.length()).trim();
            }
        }
        return "";
    }
    
    /**
     * Helper method to extract just the date portion (yyyy-MM-dd) from a full date string.
     * Handles formats like "Wed Nov 12 12:34:56 PST 2025" and extracts just the date.
     * If already in yyyy-MM-dd format, returns as is.
     * 
     * @param fullDate the full date string from the control
     * @return date in yyyy-MM-dd format, or empty string if parsing fails
     */
    private String extractDateOnly(String fullDate) {
        if (fullDate == null || fullDate.isEmpty()) {
            return "";
        }
        
        // If already in yyyy-MM-dd format, return as is
        if (fullDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return fullDate;
        }
        
        // Try to parse various date formats and extract yyyy-MM-dd
        try {
            // Try common Java Date.toString() format: "Wed Nov 12 12:34:56 PST 2025"
            String[] parts = fullDate.split(" ");
            if (parts.length >= 6) {
                // Format: Day Month Day Time Timezone Year
                String month = parts[1];
                String day = parts[2];
                String year = parts[5];
                
                // Convert month name to number
                String monthNum = getMonthNumber(month);
                if (!monthNum.isEmpty()) {
                    return year + "-" + monthNum + "-" + String.format("%02d", Integer.parseInt(day));
                }
            }
        } catch (Exception e) {
            // Fall through to return empty string
        }
        
        return "";
    }
    
    /**
     * Helper to convert month name to number (e.g., "Jan" -> "01")
     */
    private String getMonthNumber(String monthName) {
        switch (monthName.toLowerCase()) {
            case "jan": return "01";
            case "feb": return "02";
            case "mar": return "03";
            case "apr": return "04";
            case "may": return "05";
            case "jun": return "06";
            case "jul": return "07";
            case "aug": return "08";
            case "sep": return "09";
            case "oct": return "10";
            case "nov": return "11";
            case "dec": return "12";
            default: return "";
        }
    }
    
    
    /**
     * Deletes an internship opportunity (only allowed if pending status).
     * Similar to deleting applications - removes from CSV and in-memory list.
     * 
     * @param internshipID the internship to delete
     */
    private void deleteInternshipOpportunity(String internshipID) {
        try {
            int confirm = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to delete internship opportunity " + internshipID + "?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                intCtrl.deleteInternshipOpportunity(internshipID);
                JOptionPane.showMessageDialog(frame, "Internship opportunity deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                checkMyInternshipOppStatus(); // Refresh the list
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error deleting internship: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package Frontend_UI.Boundary;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import Backend.Control.InternshipControl;
import Backend.Control.LoginControl;
import Frontend_UI.Helper.Filter;

/**
 * Abstract base class for all user interface boundary classes.
 * Provides common functionality for viewing internships, changing passwords,
 * and applying filters. Uses Swing components for GUI interaction.
 * Subclasses implement role-specific UI (Student, Company Rep, Career Staff).
 * 
 * @author Allen
 * @version 1.0
 */
public abstract class AbstractCLI {
    /** The internship control for managing internship operations */
    protected InternshipControl intCtrl;
    
    /** The login control for authentication and password management */
    protected LoginControl loginCtrl;
    
    /** Current filter settings for internship searches */
    protected Filter filter = null;

    /**
     * Constructs an AbstractCLI with the specified internship control.
     * 
     * @param intCtrl The internship control instance
     */
    public AbstractCLI(InternshipControl intCtrl) {
        this.intCtrl = intCtrl;
    }

    /** Implementors should show their UI (window/dialogs). */
    public abstract void show();

    /**
     * Sets the login control for this CLI.
     * 
     * @param loginCtrl The login control instance
     */
    public void setLoginControl(LoginControl loginCtrl) {
        this.loginCtrl = loginCtrl;
    }

    /**
     * Logs out the current user and closes any open popup windows.
     */
    public void logout() {
        // Close any logged-in popup left open by the login flow
        Frontend_UI.Helper.UIHelper.closeLoggedInPopup();
        JOptionPane.showMessageDialog(null, "Logged out (frontend).", "Logout", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays a dialog for the user to change their password.
     * Prompts for old password and new password, then updates via login control.
     */
    public void changePassword() {
        if (loginCtrl == null) {
            JOptionPane.showMessageDialog(null, "Password change is not available in this context.", "Not available", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JPanel panel = new JPanel(new GridLayout(0,1));
        JPasswordField oldP = new JPasswordField();
        JPasswordField newP = new JPasswordField();
        panel.add(new JLabel("Original Password:"));
        panel.add(oldP);
        panel.add(new JLabel("New Password:"));
        panel.add(newP);
        int res = JOptionPane.showConfirmDialog(null, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                loginCtrl.changePassword(new String(oldP.getPassword()), new String(newP.getPassword()));
                JOptionPane.showMessageDialog(null, "Password changed.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Displays a UI for viewing and filtering internship opportunities.
     * Includes sorting controls and filtering options for company, level, and majors.
     */
    public void viewFilteredInternshipOpportunities() {
        if (intCtrl == null) {
            JOptionPane.showMessageDialog(null, "Internship control not available.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Panel container
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        // Sort controls row
        JPanel sortRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortRow.setBorder(BorderFactory.createTitledBorder("Sort"));
        String[] sortOptions = {"title","companyName","openDate","numberOfSlots"};
        JComboBox<String> sortCombo = new JComboBox<>(sortOptions);
        if (filter != null) sortCombo.setSelectedItem(filter.getFilterType());
        JToggleButton ascToggle = new JToggleButton("Ascending", filter == null ? true : filter.isAscending());
        ascToggle.addItemListener(e -> ascToggle.setText(ascToggle.isSelected()?"Ascending":"Descending"));
        sortRow.add(new JLabel("Order by:"));
        sortRow.add(sortCombo);
        sortRow.add(ascToggle);
        root.add(sortRow);

        // FilterIn block
        JPanel filterBlock = new JPanel();
        filterBlock.setLayout(new GridBagLayout());
        filterBlock.setBorder(BorderFactory.createTitledBorder("Optional Filters"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;

        // Preferred Majors dropdown (loaded from majors.csv)
        // For students, this filter is disabled as they can only see their major's internships
        List<String> allMajors = loadMajorsFromCSV();
        JComboBox<String> majorCombo = new JComboBox<>();
        majorCombo.addItem("(Any)");
        for (String m : allMajors) majorCombo.addItem(m);
        
        // Check if user is a student and disable the major filter
        boolean isStudent = false;
        try {
            isStudent = intCtrl.getLoggedInStudentYear() != null;
        } catch (Exception ex) { /* not a student */ }
        
        if (isStudent) {
            majorCombo.setEnabled(false);
            majorCombo.setToolTipText("Students can only view internships matching their major(s)");
        }
        
        filterBlock.add(new JLabel("Preferred Major:"), gbc);
        gbc.gridx = 1;
        filterBlock.add(majorCombo, gbc);

        // Company names dropdown (from backend helper)
        gbc.gridx = 0; gbc.gridy++;
        JComboBox<String> companyCombo = new JComboBox<>();
        companyCombo.addItem("(Any)");
        try {
            List<String> companies = intCtrl.getVisibleCompanyNames();
            for (String c : companies) companyCombo.addItem(c);
        } catch (Exception ex) {
            // ignore populate failure
        }
        filterBlock.add(new JLabel("Company:"), gbc);
        gbc.gridx = 1;
        filterBlock.add(companyCombo, gbc);

        // Internship Level dropdown (allow all; show note for Y1/2 students)
        gbc.gridx = 0; gbc.gridy++;
        JComboBox<String> levelCombo = new JComboBox<>();
        Integer studentYear = null;
        try { studentYear = intCtrl.getLoggedInStudentYear(); } catch (Exception ex) { /* ignore */ }
        if (studentYear != null && studentYear < 3) {
            // Show eligibility note for Y1/2 students
            gbc.gridwidth = 2;
            JLabel note = new JLabel("Note: Y1/2 are not eligible for Intermediate or Advanced internships.");
            note.setForeground(new Color(120, 0, 0));
            filterBlock.add(note, gbc);
            gbc.gridy++;
            gbc.gridwidth = 1;
        }
        levelCombo.addItem("(Any)");
        levelCombo.addItem("Basic");
        levelCombo.addItem("Intermediate");
        levelCombo.addItem("Advanced");
        filterBlock.add(new JLabel("Internship Level:"), gbc);
        gbc.gridx = 1;
        filterBlock.add(levelCombo, gbc);

        root.add(filterBlock);

        // Override block (ID or Title overrides other filters if filled)
        JPanel overrideBlock = new JPanel(new GridBagLayout());
        overrideBlock.setBorder(BorderFactory.createTitledBorder("Direct Lookup (overrides filters)"));
        GridBagConstraints ogbc = new GridBagConstraints();
        ogbc.insets = new Insets(4,4,4,4); ogbc.anchor = GridBagConstraints.WEST; ogbc.gridx=0; ogbc.gridy=0;
        JTextField idField = new JTextField(12);
        JTextField titleField = new JTextField(12);
        overrideBlock.add(new JLabel("Internship ID:"), ogbc);
        ogbc.gridx=1; overrideBlock.add(idField, ogbc);
        ogbc.gridx=0; ogbc.gridy++; overrideBlock.add(new JLabel("Internship Title:"), ogbc);
        ogbc.gridx=1; overrideBlock.add(titleField, ogbc);
        root.add(overrideBlock);

        int res = JOptionPane.showConfirmDialog(null, root, "Filter Internships", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String chosenFilterType = (String) sortCombo.getSelectedItem();
        boolean ascending = ascToggle.isSelected();
        Map<String, List<String>> filterIn = new HashMap<>();

        // Direct override: if ID or Title provided, ignore other filters except sort
        String idOverride = idField.getText().trim();
        String titleOverride = titleField.getText().trim();
        if (!idOverride.isEmpty()) {
            filterIn.put("internshipID", List.of(idOverride));
        } else if (!titleOverride.isEmpty()) {
            filterIn.put("internshipTitle", List.of(titleOverride));
        } else {
            // Optional filters
            String majorSel = (String) majorCombo.getSelectedItem();
            if (majorSel != null && !majorSel.equals("(Any)")) {
                filterIn.put("preferredMajors", List.of(majorSel));
            }
            String companySel = (String) companyCombo.getSelectedItem();
            if (companySel != null && !companySel.equals("(Any)")) {
                filterIn.put("companyName", List.of(companySel));
            }
            String levelSel = (String) levelCombo.getSelectedItem();
            if (levelSel != null && !levelSel.equals("(Any)")) {
                filterIn.put("internshipLevel", List.of(levelSel));
            }
        }

        filter = new Filter(chosenFilterType, ascending, filterIn);
        renderInternshipList(intCtrl.getApprovedVisibleInternshipOpportunitiesForDisplay(filter.getFilterType(), filter.isAscending(), filter.getFilterIn()));
    }

    private List<String> loadMajorsFromCSV() {
        List<String> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("Code/Backend/Lib/majors.csv"))) {
            String line = br.readLine(); // header (maybe)
            while ((line = br.readLine()) != null) {
                String m = line.trim();
                if (m.isEmpty()) continue;
                if (m.startsWith("\"") && m.endsWith("\"")) m = m.substring(1, m.length()-1);
                out.add(m);
            }
        } catch (IOException e) {
            // silent fallback: empty list
        }
        return out;
    }

    private void renderInternshipList(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No internships found.");
            return;
        }
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        // (Apply availability is checked per-row using backend helper)

        // Header row (use GridBag so actions column can be fixed width and other columns align)
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(240,240,240));
        GridBagConstraints hgb = new GridBagConstraints();
        hgb.insets = new Insets(4,6,4,6);
        hgb.gridy = 0;
        hgb.gridx = 0; hgb.weightx = 0; hgb.fill = GridBagConstraints.NONE; header.add(new JLabel("Actions", SwingConstants.CENTER), hgb);
        hgb.gridx = 1; hgb.weightx = 0.15; hgb.fill = GridBagConstraints.HORIZONTAL; header.add(new JLabel("ID", SwingConstants.CENTER), hgb);
        hgb.gridx = 2; hgb.weightx = 0.35; header.add(new JLabel("Title", SwingConstants.CENTER), hgb);
        hgb.gridx = 3; hgb.weightx = 0.12; header.add(new JLabel("Level", SwingConstants.CENTER), hgb);
        hgb.gridx = 4; hgb.weightx = 0.2; header.add(new JLabel("Company", SwingConstants.CENTER), hgb);
        hgb.gridx = 5; hgb.weightx = 0.18; header.add(new JLabel("Preferred Majors", SwingConstants.CENTER), hgb);
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.GRAY));
        listPanel.add(header);

        for (String line : lines) {
            String id = parseFieldByPrefix(line, "internshipID", false);
            String title = parseField(line, "internshipTitle");
            String company = parseField(line, "companyName");
            String level = parseField(line, "internshipLevel");
            String majorsRaw = parseField(line, "preferredMajors");
            String majors = formatMajors(majorsRaw);

            JPanel row = new JPanel(new GridBagLayout());
            GridBagConstraints r = new GridBagConstraints();
            r.insets = new Insets(6,6,6,6);
            r.gridy = 0;
            // Actions column (fixed width)
            r.gridx = 0; r.weightx = 0; r.fill = GridBagConstraints.NONE;
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            JButton details = new JButton("Details");
            String finalId = id;
            details.addActionListener(e -> showInternshipDetails(finalId));
            JButton applyBtn = new JButton("Apply");
            applyBtn.addActionListener(e -> onApply(finalId));
            boolean canApplyForThis = false;
            try { canApplyForThis = (finalId != null && intCtrl.canCurrentLoggedInStudentApply(finalId)); } catch (Exception ex) { canApplyForThis = false; }
            applyBtn.setEnabled(canApplyForThis);
            if (!applyBtn.isEnabled()) applyBtn.setToolTipText("You must be a student who meets requirements to apply");
            actions.add(details);
            actions.add(applyBtn);
            actions.setPreferredSize(new Dimension(220, 28));
            row.add(actions, r);

            // ID
            r.gridx = 1; r.weightx = 0.15; r.fill = GridBagConstraints.HORIZONTAL; row.add(new JLabel(id != null ? id : ""), r);
            // Title
            r.gridx = 2; r.weightx = 0.35; row.add(new JLabel(title != null ? title : ""), r);
            // Level
            r.gridx = 3; r.weightx = 0.12; row.add(new JLabel(level != null ? level : ""), r);
            // Company
            r.gridx = 4; r.weightx = 0.2; row.add(new JLabel(company != null ? company : ""), r);
            // Majors
            r.gridx = 5; r.weightx = 0.18; row.add(new JLabel(majors), r);
            row.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY));
            listPanel.add(row);
        }

        JScrollPane sp = new JScrollPane(listPanel);
        sp.setPreferredSize(new Dimension(900,400));
        JOptionPane.showMessageDialog(null, sp, "Internships", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handles application submission for an internship.
     * Default implementation shows "not available" - subclasses like StudentCLI should override.
     * 
     * @param internshipID The ID of the internship to apply for
     */
    protected void onApply(String internshipID) {
        JOptionPane.showMessageDialog(null, "Applying is not available here.", "Not available", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInternshipDetails(String internshipID) {
        if (internshipID == null || internshipID.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Unable to determine Internship ID for details.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            List<String> details = intCtrl.getInternshipDetails(internshipID);
            if (details == null || details.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No details found.");
                return;
            }
            JTextArea ta = new JTextArea(String.join("\n", details));
            ta.setWrapStyleWord(true); ta.setLineWrap(true); ta.setEditable(false);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(600, 350));
            JOptionPane.showMessageDialog(null, sp, "Internship Details", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Parsing helpers from toString() line format
    private String parseField(String line, String key) {
        String marker = key + "=";
        int idx = line.indexOf(marker);
        if (idx < 0) return null;
        int start = idx + marker.length();
        // New delimiter
        String DELIM = " | ";
        int end = line.indexOf(DELIM, start);
        if (end < 0) end = line.length();
        return line.substring(start, end).trim();
    }
    private String parseFieldByPrefix(String line, String key, boolean includeEquals) {
        String marker = key + (includeEquals ? "=" : "");
        int idx = line.indexOf(marker);
        if (idx < 0) return null;
        int start = idx + marker.length();
        if (!includeEquals && start < line.length() && line.charAt(start) == '=') {
            start++;
        }
        String DELIM = " | ";
        int end = line.indexOf(DELIM, start);
        if (end < 0) end = line.length();
        return line.substring(start, end).trim();
    }

    private String formatMajors(String raw) {
        if (raw == null) return "[]";
        String r = raw.trim();
        if (r.isEmpty()) return "[]";
        // Collapse accidental double brackets e.g. "[[A, B]]" -> "[A, B]"
        while (r.startsWith("[[") && r.endsWith("]]")) {
            r = r.substring(1, r.length()-1).trim();
        }
        // If already bracketed like [A, B], normalize spacing and return
        if (r.startsWith("[") && r.endsWith("]")) {
            String inner = r.substring(1, r.length()-1).trim();
            if (inner.isEmpty()) return "[]";
            String[] items = inner.split(",");
            List<String> p = new ArrayList<>();
            for (String s : items) { String t = s.trim(); if (!t.isEmpty()) p.add(t); }
            return "[" + String.join(", ", p) + "]";
        }
        // Prefer semicolon as separator if present
        List<String> p = new ArrayList<>();
        if (r.contains(";")) {
            for (String s : r.split(";")) { String t = s.trim(); if (!t.isEmpty()) p.add(t); }
            return "[" + String.join(", ", p) + "]";
        }
        // Otherwise, split on commas if present
        if (r.contains(",")) {
            // Do not split inside existing brackets already handled above.
            for (String s : r.split(",")) { String t = s.trim(); if (!t.isEmpty()) p.add(t); }
            return "[" + String.join(", ", p) + "]";
        }
        // Fallback: single entry (may contain spaces)
        return "[" + r + "]";
    }
}

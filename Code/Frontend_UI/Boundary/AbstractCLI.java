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
 * UI base class mirroring the console AbstractCLI, but using Swing dialogs.
 * Provides common wiring for controls and shared actions (logout, changePassword, viewFilteredInternshipOpportunities).
 */
public abstract class AbstractCLI {
    protected InternshipControl intCtrl;
    protected LoginControl loginCtrl;
    protected Filter filter = null;

    public AbstractCLI(InternshipControl intCtrl) {
        this.intCtrl = intCtrl;
    }

    /** Implementors should show their UI (window/dialogs). */
    public abstract void show();

    public void setLoginControl(LoginControl loginCtrl) {
        this.loginCtrl = loginCtrl;
    }

    public void logout() {
        // Close any logged-in popup left open by the login flow
        Frontend_UI.Helper.UIHelper.closeLoggedInPopup();
        JOptionPane.showMessageDialog(null, "Logged out (frontend).", "Logout", JOptionPane.INFORMATION_MESSAGE);
    }

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
        List<String> allMajors = loadMajorsFromCSV();
        JComboBox<String> majorCombo = new JComboBox<>();
        majorCombo.addItem("(Any)");
        for (String m : allMajors) majorCombo.addItem(m);
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
        renderInternshipList(intCtrl.getAllVisibleInternshipOpportunitiesForDisplay(filter.getFilterType(), filter.isAscending(), filter.getFilterIn()));
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
        JTextArea ta = new JTextArea(String.join("\n", lines));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(600,300));
        JOptionPane.showMessageDialog(null, sp, "Internships", JOptionPane.INFORMATION_MESSAGE);
    }
}

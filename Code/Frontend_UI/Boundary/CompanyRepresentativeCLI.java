package Frontend_UI.Boundary;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Backend.Control.*;
import Frontend_UI.UIHelper;

public class CompanyRepresentativeCLI extends AbstractCLI {
    private JFrame frame;

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

            JButton approveApp = new JButton("Approve/Reject Applications");
            approveApp.addActionListener(e -> approveApplication());
            p.add(approveApp);

            JButton toggleVis = new JButton("Toggle Visibility by ID");
            toggleVis.addActionListener(e -> toggleVisibility());
            p.add(toggleVis);

            JButton logout = new JButton("Logout");
            logout.addActionListener(e -> {
                Frontend_UI.UIHelper.closeLoggedInPopup();
                frame.dispose();
            });
            p.add(logout);

            frame.setContentPane(p);
            frame.setVisible(true);
        });
    }

    // changePassword and viewFilteredInternshipOpportunities inherited from AbstractCLI

    private List<String> loadMajors() {
        List<String> majors = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("Code/Backend/Lib/majors.csv"))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String m = line.trim();
                    if (m.startsWith("\"") && m.endsWith("\"")) {
                        m = m.substring(1, m.length() - 1);
                    }
                    majors.add(m);
                }
            }
        } catch (IOException e) {
            // ignore, return empty
        }
        return majors;
    }

    private void createInternshipOpportunity() {
        JTextField title = new JTextField();
        JTextArea desc = new JTextArea(5,30);
        String[] levels = {"Basic","Intermediate","Advanced"};
        JComboBox<String> level = new JComboBox<>(levels);
        JTextField open = new JTextField();
        JTextField close = new JTextField();
        JTextField slots = new JTextField("1");

        List<String> allMajors = loadMajors();
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
            List<String> lines = intCtrl.getInternshipStatus();
            if (lines.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No internship opportunities found for this company representative.");
                return;
            }
            JTextArea ta = new JTextArea(String.join("\n", lines));
            ta.setEditable(false);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(600,300));
            JOptionPane.showMessageDialog(frame, sp, "My Internships", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void approveApplication() {
        String oppId = JOptionPane.showInputDialog(frame, "Enter Internship ID to view applications (leave empty to view all):");
        try {
            List<String> lines = intCtrl.viewApplications(oppId == null ? "" : oppId);
            if (lines.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No applications for the selected internship(s).");
                return;
            }
            JTextArea ta = new JTextArea(String.join("\n", lines));
            ta.setEditable(false);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(600,300));
            JOptionPane.showMessageDialog(frame, sp, "Applications", JOptionPane.INFORMATION_MESSAGE);

            String appNumStr = JOptionPane.showInputDialog(frame, "Enter Application Number to approve (or leave empty to cancel):");
            if (appNumStr == null || appNumStr.trim().isEmpty()) return;
            int appNum = Integer.parseInt(appNumStr.trim());
            String internshipID = JOptionPane.showInputDialog(frame, "Enter Internship ID for this application:");
            if (internshipID == null || internshipID.trim().isEmpty()) return;
            intCtrl.approveApplicationAsCompanyRep(appNum);
            JOptionPane.showMessageDialog(frame, "Application approved.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleVisibility() {
        String id = JOptionPane.showInputDialog(frame, "Enter Internship ID to toggle visibility:");
        if (id == null || id.trim().isEmpty()) return;
        try {
            intCtrl.changeVisibilityByID(id);
            JOptionPane.showMessageDialog(frame, "Toggled visibility for: " + id);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

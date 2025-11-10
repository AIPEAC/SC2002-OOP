package Frontend_UI.Boundary;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import Backend.Control.*;
import Frontend_UI.Helper.UIHelper;

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

            JButton toggleVis = new JButton("Toggle Visibility by ID");
            toggleVis.addActionListener(e -> toggleVisibility());
            p.add(toggleVis);

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
                JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                actionsPanel.add(checkApps);
                actionsPanel.setPreferredSize(new Dimension(180, 28));
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
                JButton approve = new JButton("Approve");
                JButton reject = new JButton("Reject");

                // Disable if already approved/rejected
                boolean isPending = "pending".equalsIgnoreCase(appStatus);
                approve.setEnabled(isPending);
                reject.setEnabled(isPending);

                String finalAppNum = appNumStr;
                approve.addActionListener(e -> {
                    try {
                        int num = Integer.parseInt(finalAppNum);
                        intCtrl.approveApplicationForInternship(internshipID, num);
                        JOptionPane.showMessageDialog(frame, "Approved application: " + num);
                        approve.setEnabled(false); reject.setEnabled(false);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

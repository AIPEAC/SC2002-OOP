package Frontend_UI.Boundary;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import Backend.Control.*;
import Frontend_UI.UIHelper;

public class CareerStaffCLI {
    private ApplicationControl appCtrl;
    private ReportControl reportCtrl;
    private UserControl userCtrl;
    private LoginControl loginCtrl;
    private InternshipControl intCtrl;
    private JFrame frame;

    public CareerStaffCLI(ApplicationControl appCtrl, InternshipControl intCtrl, ReportControl reportCtrl, UserControl userCtrl, LoginControl loginCtrl) {
        this.appCtrl = appCtrl;
        this.reportCtrl = reportCtrl;
        this.userCtrl = userCtrl;
        this.loginCtrl = loginCtrl;
        this.intCtrl = intCtrl;
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Career Staff");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(520,420);
            frame.setLocationRelativeTo(null);
            JPanel p = new JPanel(new GridLayout(0,1,5,5));

            JButton chPwd = new JButton("Change Password");
            chPwd.addActionListener(e -> changePassword());
            p.add(chPwd);

            JButton viewOpp = new JButton("View Internship Opportunities");
            viewOpp.addActionListener(e -> viewFilteredInternshipOpportunities());
            p.add(viewOpp);

            JButton viewPendingReg = new JButton("View pending registrations");
            viewPendingReg.addActionListener(e -> viewCompanyRepRegisterList());
            p.add(viewPendingReg);

            JButton viewPendingOpp = new JButton("View pending internship opportunities");
            viewPendingOpp.addActionListener(e -> viewPendingInternshipOpp());
            p.add(viewPendingOpp);

            JButton viewPendingWithdraw = new JButton("View pending withdrawal requests");
            viewPendingWithdraw.addActionListener(e -> viewPendingWithdrawal());
            p.add(viewPendingWithdraw);

            JButton genReport = new JButton("Generate reports");
            genReport.addActionListener(e -> generateReportChoice());
            p.add(genReport);

            JButton logout = new JButton("Logout");
            logout.addActionListener(e -> frame.dispose());
            p.add(logout);

            frame.setContentPane(p);
            frame.setVisible(true);
        });
    }

    private void changePassword() {
        JPanel panel = new JPanel(new GridLayout(0,1));
        JPasswordField oldP = new JPasswordField();
        JPasswordField newP = new JPasswordField();
        panel.add(new JLabel("Original Password:"));
        panel.add(oldP);
        panel.add(new JLabel("New Password:"));
        panel.add(newP);
        int res = JOptionPane.showConfirmDialog(frame, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                loginCtrl.changePassword(new String(oldP.getPassword()), new String(newP.getPassword()));
                JOptionPane.showMessageDialog(frame, "Password changed.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewCompanyRepRegisterList() {
        try {
            List<String> pending = userCtrl.getPendingCompanyRepList();
            if (pending == null || pending.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No pending company representative registrations.");
                return;
            }
            JTextArea ta = new JTextArea(String.join("\n", pending));
            ta.setEditable(false);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(600,300));
            JOptionPane.showMessageDialog(frame, sp, "Pending Company Reps", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewFilteredInternshipOpportunities() {
        try {
            List<String> lines = intCtrl.getAllVisibleInternshipOpportunitiesForDisplay(null);
            if (lines == null || lines.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No internships found.");
                return;
            }
            JTextArea ta = new JTextArea(String.join("\n", lines));
            ta.setEditable(false);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(600,300));
            JOptionPane.showMessageDialog(frame, sp, "Internships", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void approveRegister(String id) {
        try {
            userCtrl.approveRegister(id);
            JOptionPane.showMessageDialog(frame, "Approved company representative registration: " + id);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectRegister(String id) {
        try {
            userCtrl.rejectRegister(id);
            JOptionPane.showMessageDialog(frame, "Rejected company representative registration: " + id);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewPendingInternshipOpp() {
        try {
            List<String> pending = intCtrl.getPendingInternshipOpportunities();
            if (pending == null || pending.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No pending internship opportunities.");
                return;
            }
            JTextArea ta = new JTextArea(String.join("\n", pending));
            ta.setEditable(false);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(600,300));
            JOptionPane.showMessageDialog(frame, sp, "Pending Internships", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewPendingWithdrawal() {
        try {
            List<String> pending = appCtrl.getPendingWithdrawals();
            if (pending == null || pending.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No pending withdrawal requests.");
                return;
            }
            JTextArea ta = new JTextArea(String.join("\n", pending));
            ta.setEditable(false);
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(600,300));
            JOptionPane.showMessageDialog(frame, sp, "Pending Withdrawals", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReportChoice() {
        String[] options = {"Overview","Specific","Cancel"};
        int c = JOptionPane.showOptionDialog(frame, "Generate report overview (o) or specific (s)?", "Generate Report",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (c == 0) {
            boolean save = UIHelper.showYesNo("Save report to file?");
            generateReportOverview(save);
        } else if (c == 1) {
            // simple placeholder for filters
            Map<String, List<String>> filters = Map.of();
            boolean save = UIHelper.showYesNo("Save report to file?");
            generateReportSpecific(save, filters);
        }
    }

    private void approveWithdrawal(String appNum) {
        try {
            appCtrl.approveWithdrawal(appNum);
            JOptionPane.showMessageDialog(frame, "Approved withdrawal for application: " + appNum);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rejectWithdrawal(String appNum) {
        try {
            appCtrl.rejectWithdrawal(appNum);
            JOptionPane.showMessageDialog(frame, "Rejected withdrawal for application: " + appNum);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReportOverview(boolean optToSaveReport){
        try {
            List<String> lines = reportCtrl.generateReportOverview(optToSaveReport);
            JOptionPane.showMessageDialog(frame, String.join("\n", lines), "Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void generateReportSpecific(boolean optToSaveReport,Map<String,List<String>> filterIn){
        try {
            List<String> lines = reportCtrl.generateReportSpecific(optToSaveReport, filterIn);
            JOptionPane.showMessageDialog(frame, String.join("\n", lines), "Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

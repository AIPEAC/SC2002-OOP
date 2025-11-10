package Frontend_UI.Boundary;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import Backend.Control.*;
import Frontend_UI.UIHelper;

public class CareerStaffCLI extends AbstractCLI {
    private ApplicationControl appCtrl;
    private ReportControl reportCtrl;
    private UserControl userCtrl;
    private JFrame frame;

    public CareerStaffCLI(ApplicationControl appCtrl, InternshipControl intCtrl, ReportControl reportCtrl, UserControl userCtrl, LoginControl loginCtrl) {
        super(intCtrl);
        this.appCtrl = appCtrl;
        this.reportCtrl = reportCtrl;
        this.userCtrl = userCtrl;
        setLoginControl(loginCtrl);
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
            logout.addActionListener(e -> {
                Frontend_UI.UIHelper.closeLoggedInPopup();
                frame.dispose();
            });
            p.add(logout);

            frame.setContentPane(p);
            frame.setVisible(true);
        });
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
            // After viewing, ask if staff wants to approve/reject an ID (mirror console flow)
            String id = JOptionPane.showInputDialog(frame, "Enter ID to approve/reject (leave blank to skip):");
            if (id != null && !id.trim().isEmpty()) {
                String[] opts = {"Approve","Reject","Cancel"};
                int c = JOptionPane.showOptionDialog(frame, "Approve or reject this registration?", "Approve/Reject",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
                if (c == 0) {
                    approveRegister(id.trim());
                } else if (c == 1) {
                    rejectRegister(id.trim());
                }
            }
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
            // Ask for an internship ID to approve/reject (mirror console)
            String oppId = JOptionPane.showInputDialog(frame, "Enter Internship ID to approve/reject (leave blank to skip):");
            if (oppId != null && !oppId.trim().isEmpty()) {
                String[] opts = {"Approve","Reject","Cancel"};
                int c = JOptionPane.showOptionDialog(frame, "Approve or reject this internship?", "Approve/Reject",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
                if (c == 0) {
                    try { intCtrl.approveInternshipCreationByID(oppId.trim()); JOptionPane.showMessageDialog(frame, "Approved internship: " + oppId.trim()); } catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
                } else if (c == 1) {
                    try { intCtrl.rejectInternshipCreationByID(oppId.trim()); JOptionPane.showMessageDialog(frame, "Rejected internship: " + oppId.trim()); } catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
                }
            }
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
            // Ask for application number to approve/reject (mirror console flow)
            String appNumStr = JOptionPane.showInputDialog(frame, "Enter Application Number to approve/reject (leave blank to skip):");
            if (appNumStr != null && !appNumStr.trim().isEmpty()) {
                String[] opts = {"Approve","Reject","Cancel"};
                int c = JOptionPane.showOptionDialog(frame, "Approve or reject this withdrawal?", "Approve/Reject",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
                if (c == 0) {
                    approveWithdrawal(appNumStr.trim());
                } else if (c == 1) {
                    rejectWithdrawal(appNumStr.trim());
                }
            }
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

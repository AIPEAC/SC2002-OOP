package Frontend_UI.Boundary;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import Backend.Control.*;
import Frontend_UI.Helper.UIHelper;

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
                Frontend_UI.Helper.UIHelper.closeLoggedInPopup();
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
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            for (String line : pending) {
                JPanel row = new JPanel(new BorderLayout(8,8));
                JLabel lbl = new JLabel(line);
                JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton approve = new JButton("Approve");
                JButton reject = new JButton("Reject");
                String capturedLine = line; // for lambda
                approve.addActionListener(e -> {
                    String id = parseCompanyRepID(capturedLine);
                    try { approveRegister(id); JOptionPane.showMessageDialog(frame, "Approved: " + id); approve.setEnabled(false); reject.setEnabled(false); }
                    catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
                });
                reject.addActionListener(e -> {
                    String id = parseCompanyRepID(capturedLine);
                    try { rejectRegister(id); JOptionPane.showMessageDialog(frame, "Rejected: " + id); approve.setEnabled(false); reject.setEnabled(false); }
                    catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
                });
                btns.add(approve); btns.add(reject);
                row.add(lbl, BorderLayout.CENTER);
                row.add(btns, BorderLayout.EAST);
                listPanel.add(row);
            }
            JScrollPane sp = new JScrollPane(listPanel);
            sp.setPreferredSize(new Dimension(700, Math.min(400, pending.size()*40 + 40)));
            JOptionPane.showMessageDialog(frame, sp, "Pending Company Reps", JOptionPane.INFORMATION_MESSAGE);
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
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            for (String line : pending) {
                JPanel row = new JPanel(new BorderLayout(8,8));
                JLabel lbl = new JLabel(line);
                JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton approve = new JButton("Approve");
                JButton reject = new JButton("Reject");
                String capturedLine = line;
                approve.addActionListener(e -> {
                    String id = parseInternshipID(capturedLine);
                    try { intCtrl.approveInternshipCreationByID(id); JOptionPane.showMessageDialog(frame, "Approved: " + id); approve.setEnabled(false); reject.setEnabled(false); }
                    catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
                });
                reject.addActionListener(e -> {
                    String id = parseInternshipID(capturedLine);
                    try { intCtrl.rejectInternshipCreationByID(id); JOptionPane.showMessageDialog(frame, "Rejected: " + id); approve.setEnabled(false); reject.setEnabled(false); }
                    catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
                });
                btns.add(approve); btns.add(reject);
                row.add(lbl, BorderLayout.CENTER);
                row.add(btns, BorderLayout.EAST);
                listPanel.add(row);
            }
            JScrollPane sp = new JScrollPane(listPanel);
            sp.setPreferredSize(new Dimension(700, Math.min(400, pending.size()*40 + 40)));
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
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            for (String line : pending) {
                JPanel row = new JPanel(new BorderLayout(8,8));
                JLabel lbl = new JLabel(line);
                JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton approve = new JButton("Approve");
                JButton reject = new JButton("Reject");
                String capturedLine = line;
                approve.addActionListener(e -> {
                    String num = parseApplicationNumber(capturedLine);
                    try { approveWithdrawal(num); JOptionPane.showMessageDialog(frame, "Approved: " + num); approve.setEnabled(false); reject.setEnabled(false); }
                    catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
                });
                reject.addActionListener(e -> {
                    String num = parseApplicationNumber(capturedLine);
                    try { rejectWithdrawal(num); JOptionPane.showMessageDialog(frame, "Rejected: " + num); approve.setEnabled(false); reject.setEnabled(false); }
                    catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
                });
                btns.add(approve); btns.add(reject);
                row.add(lbl, BorderLayout.CENTER);
                row.add(btns, BorderLayout.EAST);
                listPanel.add(row);
            }
            JScrollPane sp = new JScrollPane(listPanel);
            sp.setPreferredSize(new Dimension(700, Math.min(400, pending.size()*40 + 40)));
            JOptionPane.showMessageDialog(frame, sp, "Pending Withdrawals", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Parsing helpers for lines produced by backend list methods ---
    private String parseCompanyRepID(String line) {
        // Expected format: "ID: <userID> | ..."
        if (line == null) return "";
        int i = line.indexOf("ID:");
        if (i >= 0) {
            int start = i + 3;
            int end = line.indexOf('|', start);
            if (end < 0) end = line.length();
            return line.substring(start, end).trim();
        }
        // fallback: take first token
        String[] parts = line.split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }

    private String parseInternshipID(String line) {
        // Expected format starts with "<internshipID> | ..." per backend
        if (line == null) return "";
        int end = line.indexOf('|');
        if (end > 0) return line.substring(0, end).trim();
        // fallback: first token
        String[] parts = line.split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }

    private String parseApplicationNumber(String line) {
        // Expected format: "Application No: <num> | ..."
        if (line == null) return "";
        int i = line.indexOf("Application No:");
        if (i >= 0) {
            int start = i + "Application No:".length();
            int end = line.indexOf('|', start);
            if (end < 0) end = line.length();
            return line.substring(start, end).trim();
        }
        // fallback: parse first integer found
        String[] toks = line.split("\\D+");
        for (String t : toks) {
            if (!t.isEmpty()) return t;
        }
        return "";
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

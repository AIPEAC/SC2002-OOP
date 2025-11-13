package Boundary;

import javax.swing.*;

import Boundary.Helper.UIHelper;
import Control.*;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Boundary class for career center staff user interface.
 * Provides functionality for staff to approve/reject company representative registrations,
 * approve/reject internship opportunities, handle student withdrawal requests,
 * and generate comprehensive reports with various filters.
 * 
 * @author Allen
 * @version 1.0
 */
public class CareerStaffCLI extends AbstractCLI {
    /** Application controller for managing student applications */
    private ApplicationControl appCtrl;
    
    /** Report controller for generating statistics */
    private ReportControl reportCtrl;
    
    /** User controller for managing company rep approvals */
    private UserControl userCtrl;
    
    /** Main frame for staff interface */
    private JFrame frame;

    /**
     * Constructs a CareerStaffCLI with all required controllers.
     * 
     * @param appCtrl the application controller
     * @param intCtrl the internship controller
     * @param reportCtrl the report controller
     * @param userCtrl the user controller
     * @param loginCtrl the login controller for password changes
     */
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
                UIHelper.closeLoggedInPopup();
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
            // Load all applications first so staff can see pending withdrawals
            appCtrl.loadAllApplicationsFromDB();
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

    // --- Parsing helpers for lines produced by control list methods ---
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
        // Expected format starts with "<internshipID> | ..." per control
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
            showFilterDialog();
        }
    }
    
    private void showFilterDialog() {
        JDialog filterDialog = new JDialog(frame, "Specific Report Filters", true);
        filterDialog.setLayout(new BorderLayout(10, 10));
        filterDialog.setSize(700, 600);
        filterDialog.setLocationRelativeTo(frame);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel titleLabel = new JLabel("Select Filters for Report Generation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Filter options panel
        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));
        
        // Get available companies and majors from control
        List<String> availableCompanies = reportCtrl.getAllCompanyNames();
        List<String> availableMajors = reportCtrl.getAllMajors();
        
        // Company Name Filter - Multi-select list
        JPanel companyPanel = new JPanel(new BorderLayout(5, 5));
        companyPanel.setBorder(BorderFactory.createTitledBorder("Filter by Company (Ctrl+Click for multiple)"));
        JList<String> companyList = new JList<>(availableCompanies.toArray(new String[0]));
        companyList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        companyList.setVisibleRowCount(5);
        JScrollPane companyScroll = new JScrollPane(companyList);
        companyScroll.setPreferredSize(new Dimension(600, 100));
        companyPanel.add(new JLabel("Select Companies:"), BorderLayout.NORTH);
        companyPanel.add(companyScroll, BorderLayout.CENTER);
        filtersPanel.add(companyPanel);
        filtersPanel.add(Box.createVerticalStrut(10));
        
        // Level Filter
        JPanel levelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        levelPanel.setBorder(BorderFactory.createTitledBorder("Filter by Level"));
        JCheckBox basicCheck = new JCheckBox("Basic");
        JCheckBox intermediateCheck = new JCheckBox("Intermediate");
        JCheckBox advancedCheck = new JCheckBox("Advanced");
        levelPanel.add(basicCheck);
        levelPanel.add(intermediateCheck);
        levelPanel.add(advancedCheck);
        filtersPanel.add(levelPanel);
        filtersPanel.add(Box.createVerticalStrut(10));
        
        // Major Filter - Multi-select list
        JPanel majorPanel = new JPanel(new BorderLayout(5, 5));
        majorPanel.setBorder(BorderFactory.createTitledBorder("Filter by Major (Ctrl+Click for multiple)"));
        JList<String> majorList = new JList<>(availableMajors.toArray(new String[0]));
        majorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        majorList.setVisibleRowCount(5);
        JScrollPane majorScroll = new JScrollPane(majorList);
        majorScroll.setPreferredSize(new Dimension(600, 100));
        majorPanel.add(new JLabel("Select Majors:"), BorderLayout.NORTH);
        majorPanel.add(majorScroll, BorderLayout.CENTER);
        filtersPanel.add(majorPanel);
        filtersPanel.add(Box.createVerticalStrut(10));
        
        // Date Filter
        JPanel datePanel = new JPanel(new BorderLayout(5, 5));
        datePanel.setBorder(BorderFactory.createTitledBorder("Filter by Opening Date"));
        JTextField dateField = new JTextField();
        dateField.setToolTipText("Enter start date in format YYYY-MM-DD (e.g., 2025-01-01)");
        JLabel dateLabel = new JLabel("From Date (YYYY-MM-DD):");
        datePanel.add(dateLabel, BorderLayout.WEST);
        datePanel.add(dateField, BorderLayout.CENTER);
        filtersPanel.add(datePanel);
        
        JScrollPane scrollPane = new JScrollPane(filtersPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton generateButton = new JButton("Generate Report");
        JButton clearButton = new JButton("Clear All");
        JButton cancelButton = new JButton("Cancel");
        
        generateButton.addActionListener(e -> {
            Map<String, List<String>> filters = new HashMap<>();
            
            // Collect company filter from list selection
            List<String> selectedCompanies = companyList.getSelectedValuesList();
            if (!selectedCompanies.isEmpty()) {
                filters.put("CompanyName", selectedCompanies);
            }
            
            // Collect level filter
            List<String> levels = new ArrayList<>();
            if (basicCheck.isSelected()) levels.add("Basic");
            if (intermediateCheck.isSelected()) levels.add("Intermediate");
            if (advancedCheck.isSelected()) levels.add("Advanced");
            if (!levels.isEmpty()) {
                filters.put("Level", levels);
            }
            
            // Collect major filter from list selection
            List<String> selectedMajors = majorList.getSelectedValuesList();
            if (!selectedMajors.isEmpty()) {
                filters.put("Major", selectedMajors);
            }
            
            // Collect date filter
            String date = dateField.getText().trim();
            if (!date.isEmpty()) {
                filters.put("StartDate", Arrays.asList(date));
            }
            
            filterDialog.dispose();
            
            // Ask if user wants to save
            boolean save = UIHelper.showYesNo("Save report to file?");
            generateReportSpecific(save, filters);
        });
        
        clearButton.addActionListener(e -> {
            companyList.clearSelection();
            majorList.clearSelection();
            basicCheck.setSelected(false);
            intermediateCheck.setSelected(false);
            advancedCheck.setSelected(false);
            dateField.setText("");
        });
        
        cancelButton.addActionListener(e -> filterDialog.dispose());
        
        buttonPanel.add(generateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        filterDialog.add(mainPanel);
        filterDialog.setVisible(true);
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

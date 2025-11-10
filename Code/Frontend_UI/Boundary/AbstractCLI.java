package Frontend_UI.Boundary;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // Ask whether to reuse previous filter
        if (filter != null) {
            int reuse = JOptionPane.showConfirmDialog(null, "Use previously set filter criteria?", "Filter", JOptionPane.YES_NO_OPTION);
            if (reuse == JOptionPane.YES_OPTION) {
                renderInternshipList(intCtrl.getAllVisibleInternshipOpportunitiesForDisplay(filter.getFilterType(), filter.isAscending(), filter.getFilterIn()));
                return;
            }
        }
        String filterType = JOptionPane.showInputDialog(null, "Enter filter type (title, companyName, openDate, numberOfSlots):", "title");
        if (filterType == null) return;
        String[] orders = {"asc","desc"};
        String order = (String) JOptionPane.showInputDialog(null, "Sorting order:", "Order", JOptionPane.PLAIN_MESSAGE, null, orders, orders[0]);
        if (order == null) return;
        boolean ascending = "asc".equalsIgnoreCase(order);
        Map<String, List<String>> filterIn = new HashMap<>();
        // Future: collect additional filtering criteria into filterIn via dialogs.
        filter = new Filter(filterType, ascending, filterIn);
        renderInternshipList(intCtrl.getAllVisibleInternshipOpportunitiesForDisplay(filter.getFilterType(), filter.isAscending(), filter.getFilterIn()));
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

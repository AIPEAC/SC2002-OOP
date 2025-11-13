package Boundary.Helper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class providing common UI utility methods for Swing dialogs and user interactions.
 * <p>
 * This class provides reusable UI components such as confirmation dialogs, 
 * multi-select lists, and logged-in user popup notifications. All methods are static
 * for easy access from boundary classes.
 * </p>
 * 
 * @author Allen
 * @version 1.0
 */
public class UIHelper {
    private static volatile JFrame loggedInFrame = null;
    
    /**
     * Displays a yes/no confirmation dialog.
     * 
     * @param message the message to display
     * @return true if user selected Yes, false otherwise
     */
    public static boolean showYesNo(String message) {
        int res = JOptionPane.showConfirmDialog(null, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    /**
     * Displays a dialog for multi-selecting majors.
     * 
     * @param allMajors the list of available majors
     * @return a list of selected majors, or empty list if cancelled
     */
    public static List<String> showMultiSelectMajors(List<String> allMajors) {
        if (allMajors == null || allMajors.isEmpty()) return new ArrayList<>();
        JList<String> list = new JList<>(allMajors.toArray(new String[0]));
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(300, Math.min(200, allMajors.size()*20 + 20)));
        int res = JOptionPane.showConfirmDialog(null, scroll, "Select preferred majors (multi-select)", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            return list.getSelectedValuesList();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Displays a popup indicating the currently logged-in user.
     * 
     * @param userID the ID of the logged-in user
     * @param identity the role/identity of the logged-in user
     */
    public static void showLoggedInPopup(String userID, String identity) {
        SwingUtilities.invokeLater(() -> {
            // Dispose existing popup if present
            if (loggedInFrame != null) {
                try { loggedInFrame.dispose(); } catch (Exception ex) { /* ignore */ }
                loggedInFrame = null;
            }

            JFrame frame = new JFrame("Logged in");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(350,150);
            frame.setLocationRelativeTo(null);
            JPanel p = new JPanel();
            p.setLayout(new BorderLayout(10,10));
            JLabel lbl = new JLabel("Logged in as: " + userID + " (" + identity + ")");
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            p.add(lbl, BorderLayout.CENTER);
            JButton close = new JButton("Close");
            close.addActionListener(e -> frame.dispose());
            JPanel bp = new JPanel();
            bp.add(close);
            p.add(bp, BorderLayout.SOUTH);
            frame.setContentPane(p);
            loggedInFrame = frame;
            frame.setVisible(true);
        });
    }

    /**
     * Closes the logged-in user popup if it is currently displayed.
     */
    public static void closeLoggedInPopup() {
        SwingUtilities.invokeLater(() -> {
            if (loggedInFrame != null) {
                try { loggedInFrame.dispose(); } catch (Exception ex) { /* ignore */ }
                loggedInFrame = null;
            }
        });
    }
}

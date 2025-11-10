package Frontend_UI.Helper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class UIHelper {
    private static volatile JFrame loggedInFrame = null;
    public static boolean showYesNo(String message) {
        int res = JOptionPane.showConfirmDialog(null, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    public static java.util.List<String> showMultiSelectMajors(java.util.List<String> allMajors) {
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

    public static void closeLoggedInPopup() {
        SwingUtilities.invokeLater(() -> {
            if (loggedInFrame != null) {
                try { loggedInFrame.dispose(); } catch (Exception ex) { /* ignore */ }
                loggedInFrame = null;
            }
        });
    }
}

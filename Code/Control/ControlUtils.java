package Control;

class ControlUtils {
    /**
     * Parse common boolean-like strings into Boolean.
     * Returns Boolean.TRUE / Boolean.FALSE, or null if unable to parse.
     */
    public static Boolean parseBooleanLike(String s) {
        if (s == null) return null;
        String v = s.trim().toLowerCase();
        if (v.isEmpty()) return null;
        switch (v) {
            case "y": case "yes": case "true": case "1": case "a": case "approve":
                return Boolean.TRUE;
            case "n": case "no": case "false": case "0": case "r": case "reject":
                return Boolean.FALSE;
            default:
                return null;
        }
    }
}

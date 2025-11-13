package Backend.Control.Tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing common helper methods for control layer operations.
 * <p>
 * This class contains static utility methods for parsing boolean-like strings,
 * managing CSV file initialization, and handling CSV field escaping/unescaping.
 * All CSV operations use proper quoting to handle special characters (commas,
 * quotes, and newlines) within field values, ensuring data integrity.
 * </p>
 * <p>
 * CSV Format: All fields are wrapped in double quotes. Internal quotes are
 * escaped by doubling them (e.g., "He said ""Hello""" represents: He said "Hello").
 * This follows RFC 4180 CSV standards.
 * </p>
 * 
 * @author Allen
 * @version 2.0
 */
public class ControlUtils {
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

    /**
     * Ensure a CSV file exists with the given header and that the file ends with a newline.
     * If the file does not exist or is empty, it will be created with the header followed by a newline.
     * If the file exists and does not end with a newline, a newline will be appended.
     */
    public static void ensureCsvPrepared(String filePath, String header) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                String content = header + System.lineSeparator();
                fos.write(content.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }
        if (file.length() == 0) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                String content = header + System.lineSeparator();
                fos.write(content.getBytes(StandardCharsets.UTF_8));
            }
            return;
        }
        ensureTrailingNewline(filePath);
    }

    /**
     * Append a newline to the file if the last byte is not already a newline ("\n").
     * Treats '\n' as the definitive newline marker; supports CRLF by accepting '\r' before '\n'.
     */
    public static void ensureTrailingNewline(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) return;
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            long len = raf.length();
            if (len == 0) return;
            raf.seek(len - 1);
            int last = raf.read();
            if (last != '\n') {
                raf.seek(len);
                raf.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * Escape a CSV field by wrapping it in double quotes and escaping any internal quotes.
     * This ensures that commas, newlines, and quotes within the field don't break CSV parsing.
     * 
     * @param field the field to escape
     * @return the escaped field wrapped in quotes, or empty string if field is null
     */
    public static String escapeCsvField(String field) {
        if (field == null) {
            return "\"\"";
        }
        // Escape double quotes by doubling them, then wrap in quotes
        String escaped = field.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    /**
     * Unescape a CSV field by removing surrounding quotes and unescaping internal quotes.
     * This reverses the escaping done by escapeCsvField.
     * All fields are expected to be quoted.
     * 
     * @param field the field to unescape
     * @return the unescaped field
     */
    public static String unescapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        String trimmed = field.trim();
        // All fields should be wrapped in quotes
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            // Remove surrounding quotes
            String unquoted = trimmed.substring(1, trimmed.length() - 1);
            // Unescape doubled quotes
            return unquoted.replace("\"\"", "\"");
        }
        // If not quoted, return as-is (shouldn't happen with new format)
        return trimmed;
    }

    /**
     * Split a CSV line while respecting quoted fields that may contain commas.
     * This properly handles fields wrapped in quotes.
     * 
     * @param line the CSV line to split
     * @return array of fields with quotes removed where appropriate
     */
    public static String[] splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '\"') {
                // Check if this is an escaped quote (doubled)
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    currentField.append('\"');
                    i++; // Skip the next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // End of field
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // Add the last field
        fields.add(currentField.toString());
        
        return fields.toArray(new String[0]);
    }
}

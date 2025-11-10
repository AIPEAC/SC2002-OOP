package Backend.Control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Utility class providing common helper methods for control layer operations.
 * <p>
 * This class contains static utility methods for parsing boolean-like strings,
 * managing CSV file initialization, and updating CSV records. These methods
 * support the data persistence operations used throughout the control layer.
 * </p>
 * 
 * @author Allen
 * @version 1.0
 */
class ControlUtils {
    /**
     * Parse common boolean-like strings into Boolean.
     * Returns Boolean.TRUE / Boolean.FALSE, or null if unable to parse.
     */
    static Boolean parseBooleanLike(String s) {
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
    static void ensureCsvPrepared(String filePath, String header) throws IOException {
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
    static void ensureTrailingNewline(String filePath) throws IOException {
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
}

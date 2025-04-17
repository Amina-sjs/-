package util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class ActivityLogger {
    private static final String LOG_FILE = "data/activity_log.csv";

    public static void log(String user, String action, String details) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            String line = String.format("%s,%s,%s,%s\n",
                LocalDateTime.now(), user, action, details);
            writer.write(line);
        } catch (IOException e) {
            System.out.println("Failed to log activity.");
        }
    }
}

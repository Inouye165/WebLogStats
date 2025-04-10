import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class LogAnalyzer {
    private ArrayList<LogEntry> records;
    // Add fields to track min and max dates
    private Date minDate = null;
    private Date maxDate = null;

    public LogAnalyzer() {
        records = new ArrayList<LogEntry>();
    }

    /**
     * Reads log entries from a file using WebLogParser. Clears previous records
     * and finds the minimum and maximum dates in the file.
     * @param filename The name of the log file to read.
     */
    public void readFile(String filename) {
        records.clear(); // Clear previous records
        minDate = null;  // Reset min/max dates
        maxDate = null;

        try {
            List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
            for (String line : lines) {
                try {
                     if (line == null || line.trim().isEmpty()) continue;

                     LogEntry entry = WebLogParser.parseEntry(line);
                     records.add(entry);

                     // Track min and max dates
                     Date currentDate = entry.getAccessTime();
                     if (currentDate != null) {
                         if (minDate == null || currentDate.before(minDate)) {
                             minDate = currentDate;
                         }
                         if (maxDate == null || currentDate.after(maxDate)) {
                             maxDate = currentDate;
                         }
                     }
                } catch (Exception e) {
                    System.err.println("Error parsing line: '" + line + "' - " + e.getMessage());
                }
            }
            System.out.println("Successfully read " + records.size() + " records from " + filename);
            if (minDate != null && maxDate != null) {
                 System.out.println("Log date range: " + minDate + " to " + maxDate);
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + filename + " - " + e.getMessage());
        } catch (Exception e) {
             System.err.println("An unexpected error occurred during file processing: " + e.getMessage());
             e.printStackTrace();
        }
    }

    // --- Getters for min/max dates ---
    public Date getMinDate() {
        return minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    // --- Other existing methods ---
    public int countUniqueIPs() { /* ... no change ... */
        HashSet<String> uniqueIPs = new HashSet<>();
        for (LogEntry le : records) { uniqueIPs.add(le.getIpAddress()); }
        return uniqueIPs.size();
    }
    public void printAllHigherThanNum(int num) { /* ... no change ... */
        System.out.println("\n--- Log entries with status code > " + num + " ---");
        int count = 0;
        for (LogEntry le : records) { if (le.getStatusCode() > num) { System.out.println(le); count++; } }
        if (count == 0) System.out.println("None found.");
        System.out.println("--- End Status Code > " + num + " ---");
    }
    public ArrayList<String> uniqueIPsInRange(int low, int high) { /* ... no change ... */
        HashSet<String> uniqueIPs = new HashSet<>();
        for (LogEntry le : records) { int status = le.getStatusCode(); if (status >= low && status <= high) uniqueIPs.add(le.getIpAddress()); }
        return new ArrayList<>(uniqueIPs);
    }
    public int countUniqueIPsInRange(int low, int high) { /* ... no change ... */ return uniqueIPsInRange(low, high).size(); }
    public void printAll() { /* ... no change ... */ for (LogEntry le : records) System.out.println(le); }
    public ArrayList<String> uniqueIPVisitsOnDay(String someday) { /* ... no change ... */
        HashSet<String> uniqueIPsOnDay = new HashSet<>();
        for (LogEntry le : records) { Date d = le.getAccessTime(); if (d != null && d.toString().contains(someday)) uniqueIPsOnDay.add(le.getIpAddress()); }
        return new ArrayList<>(uniqueIPsOnDay);
    }
}
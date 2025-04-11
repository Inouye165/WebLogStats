// FILE: LogAnalyzer.java

import java.util.*; // Includes ArrayList, HashSet, Date, List
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Date; // Explicit import for clarity
import java.text.SimpleDateFormat; // Needed for date comparison method
import java.util.Locale;         // Needed by SimpleDateFormat

public class LogAnalyzer {
    private ArrayList<LogEntry> records;
    // Fields to track min and max dates
    private Date minDate = null;
    private Date maxDate = null;

    public LogAnalyzer() {
        records = new ArrayList<LogEntry>();
    }

    /**
     * Reads log entries from a file using WebLogParser. Clears previous records
     * and finds the minimum and maximum dates in the file.
     * @param filename The name of the log file to read.
     * @throws IOException If an error occurs reading the file.
     */
    public void readFile(String filename) throws IOException {
        records.clear(); // Clear previous records
        minDate = null;  // Reset min/max dates
        maxDate = null;

        List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);

        for (String line : lines) {
            try {
                 if (line == null || line.trim().isEmpty()) continue; // Skip empty lines
                 LogEntry entry = WebLogParser.parseEntry(line);
                 records.add(entry);
                 Date currentDate = entry.getAccessTime();
                 if (currentDate != null) {
                     if (minDate == null || currentDate.before(minDate)) minDate = currentDate;
                     if (maxDate == null || currentDate.after(maxDate)) maxDate = currentDate;
                 }
            } catch (Exception e) {
                System.err.println("Error parsing line: '" + line + "' - " + e.getMessage());
            }
        }
        System.out.println("Successfully read " + records.size() + " records from " + filename);
        if (minDate != null && maxDate != null) System.out.println("Log date range: " + minDate + " to " + maxDate);
    }

    // --- Getters for min/max dates ---
    public Date getMinDate() {
        return minDate;
    }
    public Date getMaxDate() {
        return maxDate;
    }

    // --- Analysis Methods ---

    /**
     * Counts the number of unique IP addresses in the log records.
     * THIS IS THE METHOD THE COMPILER IS COMPLAINING ABOUT - MAKE SURE IT'S EXACTLY LIKE THIS.
     * @return The count of unique IP addresses.
     */
    public int countUniqueIPs() {
        HashSet<String> uniqueIPs = new HashSet<>();
        for (LogEntry le : records) {
            // Ensure IP address is not null before adding (good practice)
            if (le.getIpAddress() != null) {
                uniqueIPs.add(le.getIpAddress());
            }
        }
        return uniqueIPs.size();
    }

    /**
     * Constructs a string containing all log entries with a status code
     * strictly greater than num.
     * @param num The threshold status code.
     * @return A String containing the matching log entries, or a "None found" message.
     */
    public String getAllHigherThanNum(int num) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Log entries with status code > ").append(num).append(" ---\n");
        int count = 0;
        for (LogEntry le : records) {
            if (le.getStatusCode() > num) {
                sb.append(le.toString()).append("\n");
                count++;
            }
        }
         if (count == 0) sb.append("None found.\n");
        sb.append("--- End Status Code > ").append(num).append(" ---");
        return sb.toString();
    }

    /**
     * Finds unique IP addresses that had status codes within the specified range (inclusive).
     * @param low The lower bound of the status code range.
     * @param high The upper bound of the status code range.
     * @return An ArrayList containing the unique IP addresses.
     */
    public ArrayList<String> uniqueIPsInRange(int low, int high) {
        HashSet<String> uniqueIPs = new HashSet<>();
        for (LogEntry le : records) {
            int status = le.getStatusCode();
            if (status >= low && status <= high) {
                if (le.getIpAddress() != null) uniqueIPs.add(le.getIpAddress());
            }
        }
        return new ArrayList<>(uniqueIPs);
   }

   /**
     * Counts the number of unique IP addresses that had status codes within the specified range (inclusive).
     * @param low The lower bound of the status code range.
     * @param high The upper bound of the status code range.
     * @return The count of unique IP addresses in the range.
     */
    public int countUniqueIPsInRange(int low, int high) {
         return uniqueIPsInRange(low, high).size();
    }

    /**
     * Finds unique IP addresses that accessed the site within a given date range (inclusive).
     * Compares using Date objects. Handles range correctly.
     * @param startDate The starting date of the range (inclusive).
     * @param endDate The ending date of the range (inclusive).
     * @return An ArrayList<String> of unique IP addresses visiting within that range.
     */
    public ArrayList<String> getUniqueIPsForDateRange(Date startDate, Date endDate) {
        HashSet<String> uniqueIPsInRange = new HashSet<>();
        if (startDate == null || endDate == null || startDate.after(endDate)) {
             return new ArrayList<>(); // Return empty list for invalid range
        }
        for (LogEntry le : records) {
            Date accessDate = le.getAccessTime();
            if (accessDate == null) continue;
            // Check if !before(start) AND !after(end) for inclusive range
            if (!accessDate.before(startDate) && !accessDate.after(endDate)) {
                 if (le.getIpAddress() != null) uniqueIPsInRange.add(le.getIpAddress());
            }
        }
        return new ArrayList<>(uniqueIPsInRange);
    }

   /**
     * Finds unique IP addresses that accessed the site on a specific single day.
     * @param someday A String in the format "MMM DD" (e.g., "Sep 14", "Dec 05").
     * @return An ArrayList<String> of unique IP addresses from that day.
     */
    public ArrayList<String> uniqueIPVisitsOnDay(String someday) {
        HashSet<String> uniqueIPsOnDay = new HashSet<>();
        SimpleDateFormat dayMonthFormatter = new SimpleDateFormat("MMM dd", Locale.US);
        for (LogEntry le : records) {
            Date accessDate = le.getAccessTime();
            if (accessDate == null) continue;
            try {
                String formattedDate = dayMonthFormatter.format(accessDate);
                if (formattedDate.equals(someday)) {
                    if (le.getIpAddress() != null) uniqueIPsOnDay.add(le.getIpAddress());
                }
            } catch (Exception e) { System.err.println("Err fmt date:"+accessDate+e.getMessage());}
        }
        return new ArrayList<>(uniqueIPsOnDay);
    }

    /**
     * Prints all log entries stored. (Useful for debugging)
     */
   public void printAll() {
        System.out.println("\n--- All Log Entries ---");
        if (records.isEmpty()) System.out.println("(No records loaded)");
        else for (LogEntry le : records) System.out.println(le);
        System.out.println("--- End All Log Entries ---");
    }

} // End of LogAnalyzer class
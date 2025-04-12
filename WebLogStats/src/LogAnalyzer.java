import java.util.*; // Includes ArrayList, HashSet, Date, List, HashMap, Map, Collections
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Date; // Explicit import for clarity
import java.text.SimpleDateFormat; // Needed for date comparison method
import java.util.Locale;          // Needed by SimpleDateFormat

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
                // Consider logging more specific parse exceptions if needed
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
     * Leverages countVisitsPerIP() for efficiency.
     * @return The count of unique IP addresses.
     */
    public int countUniqueIPs() {
        // The entire original body (using HashSet) is replaced by this single line:
        return countVisitsPerIP().size();
    }

     /**
      * Counts the number of times each unique IP address appears in the log records.
      * Uses a HashMap to store IP addresses (String) as keys and their counts (Integer) as values.
      * @return A HashMap mapping each IP address (String) to its visit count (Integer).
      */
     public HashMap<String, Integer> countVisitsPerIP() {
         // 1. Create an empty HashMap to store counts
         HashMap<String, Integer> counts = new HashMap<>();

         // 2. Iterate over all the log entries in records
         for (LogEntry le : records) {
             // 3. Get the IP address from the current LogEntry
             String ip = le.getIpAddress();

             // Basic null check for safety, though parser should handle valid lines
             if (ip == null) {
                 continue; // Skip if IP is somehow null
             }

             // 4. Check if the IP is already in the HashMap (Using getOrDefault is concise)
             counts.put(ip, counts.getOrDefault(ip, 0) + 1);

             // Original logic from video (also works fine):
             /*
             if (!counts.containsKey(ip)) {
                 // 5. If not, put it in the map with a count of 1
                 counts.put(ip, 1);
             } else {
                 // 6. If it is, get the current count, increment it, and put it back
                 counts.put(ip, counts.get(ip) + 1);
             }
             */
         }
         // 7. Return the completed HashMap
         return counts;
     }

    /**
     * Finds the maximum number of visits by any single IP address.
     * @param ipCounts A HashMap mapping IP addresses to their visit counts.
     * @return The highest visit count found in the map, or 0 if the map is empty.
     */
    public int mostNumberVisitsByIP(HashMap<String, Integer> ipCounts) {
        if (ipCounts == null || ipCounts.isEmpty()) {
            return 0;
        }
        // Using streams is concise for finding max value
        return ipCounts.values().stream().max(Integer::compare).orElse(0);
        /* // Iterative approach (also correct):
        int maxVisits = 0;
        for (int count : ipCounts.values()) {
            if (count > maxVisits) {
                maxVisits = count;
            }
        }
        return maxVisits;
        */
    }

    /**
     * Finds all IP addresses that had the maximum number of visits.
     * @param ipCounts A HashMap mapping IP addresses to their visit counts.
     * @return An ArrayList of IP addresses (Strings) that tie for the most visits.
     */
    public ArrayList<String> iPsMostVisits(HashMap<String, Integer> ipCounts) {
        ArrayList<String> maxVisitIPs = new ArrayList<>();
        if (ipCounts == null || ipCounts.isEmpty()) {
            return maxVisitIPs; // Return empty list
        }

        int maxVisits = mostNumberVisitsByIP(ipCounts); // Find the max count first

        // Iterate through entries to find keys matching the max value
        for (Map.Entry<String, Integer> entry : ipCounts.entrySet()) {
            if (entry.getValue() == maxVisits) { // Use == for primitive int comparison
                maxVisitIPs.add(entry.getKey());
            }
        }
        return maxVisitIPs;
    }

    /**
     * Creates a map where keys are days ("MMM dd" format) and values are lists
     * of all IP addresses that visited on that day (including duplicates).
     * @return A HashMap mapping day strings to ArrayLists of IP address strings.
     */
    public HashMap<String, ArrayList<String>> iPsForDays() {
        HashMap<String, ArrayList<String>> dayToIPs = new HashMap<>();
        // Consider making this formatter a static final field if used frequently
        SimpleDateFormat dayMonthFormatter = new SimpleDateFormat("MMM dd", Locale.US);

        for (LogEntry le : records) {
            Date accessDate = le.getAccessTime();
            String ip = le.getIpAddress();
            if (accessDate == null || ip == null) {
                continue; // Skip entries without date or IP
            }

            try {
                String dayKey = dayMonthFormatter.format(accessDate);
                // Using computeIfAbsent is efficient for map value initialization
                dayToIPs.computeIfAbsent(dayKey, k -> new ArrayList<>()).add(ip);
            } catch (Exception e) {
                 // Catch potential formatting errors, though unlikely with valid dates
                System.err.println("Error formatting date for day map: " + accessDate + " - " + e.getMessage());
            }
        }
        return dayToIPs;
    }

    /**
     * Finds the day (in "MMM dd" format) on which the most total IP visits occurred.
     * @param dayToIPs A HashMap mapping day strings to ArrayLists of IP addresses (from iPsForDays).
     * @return The day string ("MMM dd") with the highest number of IP visits, or null if the map is empty.
     */
    public String dayWithMostIPVisits(HashMap<String, ArrayList<String>> dayToIPs) {
        if (dayToIPs == null || dayToIPs.isEmpty()) {
            return null;
        }
        String busiestDay = null;
        int maxVisitsOnDay = -1; // Start at -1 to handle days with 0 visits correctly if needed

        for (Map.Entry<String, ArrayList<String>> entry : dayToIPs.entrySet()) {
            int currentDayVisits = entry.getValue().size();
            if (currentDayVisits > maxVisitsOnDay) {
                maxVisitsOnDay = currentDayVisits;
                busiestDay = entry.getKey();
            }
        }
        return busiestDay;
    }

     /**
      * Finds the IP addresses that visited the most times on a *specific* given day.
      * @param dayToIPs The map generated by iPsForDays().
      * @param day The specific day ("MMM dd" format) to analyze.
      * @return An ArrayList<String> of the IP(s) that visited most frequently on that particular day. Returns empty list if day is invalid or has no visits.
      */
     public ArrayList<String> iPsWithMostVisitsOnDay(HashMap<String, ArrayList<String>> dayToIPs, String day) {
         ArrayList<String> resultIPs = new ArrayList<>();
         // Input validation
         if (dayToIPs == null || day == null || !dayToIPs.containsKey(day)) {
             System.err.println("Warning: Day '" + day + "' not found in dayToIPs map.");
             return resultIPs; // Return empty
         }

         ArrayList<String> ipsOnSpecificDay = dayToIPs.get(day);
         if (ipsOnSpecificDay == null || ipsOnSpecificDay.isEmpty()) {
             // Day exists but has no recorded IPs
             return resultIPs; // Return empty
         }

         // 1. Count occurrences of each IP *for this specific day*
         HashMap<String, Integer> countsOnDay = new HashMap<>();
         for (String ip : ipsOnSpecificDay) {
             countsOnDay.put(ip, countsOnDay.getOrDefault(ip, 0) + 1);
         }

         // If no counts were generated (e.g., all IPs were null, though filtered earlier), exit.
         if (countsOnDay.isEmpty()) {
            return resultIPs;
         }

         // 2. Find the maximum count *within this day* using the helper method
         int maxVisitsOnThisDay = mostNumberVisitsByIP(countsOnDay);


         // 3. Find all IPs that match this maximum count
         for (Map.Entry<String, Integer> entry : countsOnDay.entrySet()) {
             if (entry.getValue() == maxVisitsOnThisDay) {
                 resultIPs.add(entry.getKey());
             }
         }

         return resultIPs;
     }

    /**
     * Constructs a string containing all log entries with a status code
     * strictly greater than num. (Existing Method)
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
     * Finds unique IP addresses that had status codes within the specified range (inclusive). (Existing Method)
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
     * Counts the number of unique IP addresses that had status codes within the specified range (inclusive). (Existing Method)
     * @param low The lower bound of the status code range.
     * @param high The upper bound of the status code range.
     * @return The count of unique IP addresses in the range.
     */
    public int countUniqueIPsInRange(int low, int high) {
         return uniqueIPsInRange(low, high).size();
    }

    /**
     * Finds unique IP addresses that accessed the site within a given date range (inclusive). (Existing Method)
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
     * Finds unique IP addresses that accessed the site on a specific single day. (Existing Method)
     * @param someday A String in the format "MMM dd" (e.g., "Sep 14", "Dec 05").
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
     * Prints all log entries stored. (Useful for debugging) (Existing Method)
     */
   public void printAll() {
        System.out.println("\n--- All Log Entries ---");
        if (records.isEmpty()) System.out.println("(No records loaded)");
        else for (LogEntry le : records) System.out.println(le);
        System.out.println("--- End All Log Entries ---");
    }

} // End of LogAnalyzer class
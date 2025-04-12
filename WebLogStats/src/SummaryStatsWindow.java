// FILE: SummaryStatsWindow.java (Modified)

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap; // For MONTH_MAP
import java.util.Map;

public class SummaryStatsWindow extends JFrame {

    private LogAnalyzer analyzer;
    private String shortFilename;

    // GUI Components for Summary
    private JLabel fileInfoLabel;
    private JTextArea summaryResultsTextArea; // Renamed for clarity

    // GUI Components for Specific Date Query
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> dayCombo;
    private JComboBox<Integer> yearCombo;
    private JButton analyzeDateButton;
    private JTextArea selectedDateResultsTextArea; // New text area

    // Data needed across methods
    private HashMap<String, ArrayList<String>> dayToIPsMap; // Make this a field

    // Month mapping (similar to LogAnalysisWindow)
    private static final Map<String, Integer> MONTH_MAP = createMonthMap();

    public SummaryStatsWindow(LogAnalyzer analyzer, String filename) {
        this.analyzer = analyzer;
        this.shortFilename = filename;

        setTitle("📊 Overall Log Statistics & Date Query Zone! 🎉");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(750, 800)); // Increased height

        initComponents(); // Create all components
        populateSummaryResults(); // Populate the main summary area
        populateDateSelectors();  // Populate year/month/day dropdowns

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Static method to create month map
    private static Map<String, Integer> createMonthMap() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("Jan", Calendar.JANUARY); map.put("Feb", Calendar.FEBRUARY);
        map.put("Mar", Calendar.MARCH);   map.put("Apr", Calendar.APRIL);
        map.put("May", Calendar.MAY);     map.put("Jun", Calendar.JUNE);
        map.put("Jul", Calendar.JULY);    map.put("Aug", Calendar.AUGUST);
        map.put("Sep", Calendar.SEPTEMBER); map.put("Oct", Calendar.OCTOBER);
        map.put("Nov", Calendar.NOVEMBER); map.put("Dec", Calendar.DECEMBER);
        return map;
    }

    private void initComponents() {
        // --- Main Layout ---
        // Use BorderLayout for overall structure
        // NORTH: File Info
        // CENTER: Split Pane (Top: Summary Results, Bottom: Date Query Area)
        setLayout(new BorderLayout(10, 10)); // Add gaps

        // --- Top Panel: File Info ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Adjust padding
        fileInfoLabel = new JLabel("Analyzing File: N/A");
        fileInfoLabel.setFont(fileInfoLabel.getFont().deriveFont(Font.BOLD, 14f));
        topPanel.add(fileInfoLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Area: Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6); // Give summary more space initially

        // --- Top of Split Pane: Summary Results Area ---
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Overall Log Summary"));
        summaryResultsTextArea = new JTextArea(20, 60); // Adjusted rows
        summaryResultsTextArea.setEditable(false);
        summaryResultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane summaryScrollPane = new JScrollPane(summaryResultsTextArea);
        summaryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        summaryPanel.add(summaryScrollPane, BorderLayout.CENTER);
        splitPane.setTopComponent(summaryPanel);

        // --- Bottom of Split Pane: Date Query Area ---
        JPanel dateQueryPanel = new JPanel(new BorderLayout(5, 5));
        dateQueryPanel.setBorder(BorderFactory.createTitledBorder("Query Specific Date"));

        // Date Selection Components Panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Select Date:"));
        monthCombo = new JComboBox<>(MONTH_MAP.keySet().toArray(new String[0]));
        dayCombo = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayCombo.addItem(i);
        yearCombo = new JComboBox<>(); // Populated later
        analyzeDateButton = new JButton("Analyze Selected Date");

        selectionPanel.add(monthCombo);
        selectionPanel.add(new JLabel("Day:"));
        selectionPanel.add(dayCombo);
        selectionPanel.add(new JLabel("Year:"));
        selectionPanel.add(yearCombo);
        selectionPanel.add(analyzeDateButton);
        dateQueryPanel.add(selectionPanel, BorderLayout.NORTH);

        // Selected Date Results Area
        selectedDateResultsTextArea = new JTextArea(10, 60); // Text area for results
        selectedDateResultsTextArea.setEditable(false);
        selectedDateResultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane selectedDateScrollPane = new JScrollPane(selectedDateResultsTextArea);
        selectedDateScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dateQueryPanel.add(selectedDateScrollPane, BorderLayout.CENTER);

        splitPane.setBottomComponent(dateQueryPanel);

        // Add Split Pane to the frame's center
        add(splitPane, BorderLayout.CENTER);

        // --- Action Listener ---
        analyzeDateButton.addActionListener(e -> analyzeSelectedDate());
    }

    // Populate the main summary text area (logic moved here)
    private void populateSummaryResults() {
        fileInfoLabel.setText("Analyzing File: " + shortFilename);

        StringBuilder sb = new StringBuilder();
        sb.append("🚀 Welcome to the Log Stats Extravaganza! 🚀\n");
        sb.append("=============================================\n\n");

        // Perform calculations first
        HashMap<String, Integer> ipCountsMap = analyzer.countVisitsPerIP();
        int totalUniqueIPs = ipCountsMap.size();
        int maxVisits = analyzer.mostNumberVisitsByIP(ipCountsMap);
        ArrayList<String> ipsWithMaxVisits = analyzer.iPsMostVisits(ipCountsMap);
        this.dayToIPsMap = analyzer.iPsForDays(); // Initialize the field here
        String busiestDay = analyzer.dayWithMostIPVisits(this.dayToIPsMap); // Use field
        ArrayList<String> busiestDayFrequentIPs = new ArrayList<>();
        int maxVisitsOnBusiestDay = 0;

        if (busiestDay != null && this.dayToIPsMap.containsKey(busiestDay)) { // Use field
            busiestDayFrequentIPs = analyzer.iPsWithMostVisitsOnDay(this.dayToIPsMap, busiestDay); // Use field
            HashMap<String, Integer> countsOnBusiestDay = new HashMap<>();
            ArrayList<String> ipsOnBusiest = this.dayToIPsMap.get(busiestDay); // Use field
            if (ipsOnBusiest != null) {
                for (String ip : ipsOnBusiest) {
                    countsOnBusiestDay.put(ip, countsOnBusiestDay.getOrDefault(ip, 0) + 1);
                }
                maxVisitsOnBusiestDay = analyzer.mostNumberVisitsByIP(countsOnBusiestDay);
            }
        }

        // Format Output (same as before)
        sb.append("--- 📈 IP Visit Frequency Analysis --- \n");
        sb.append("Total Unique IP Addresses Found: ").append(totalUniqueIPs).append("\n");
        sb.append("🥇 Maximum Visits by a Single IP: ").append(maxVisits).append("\n");
        sb.append("🏆 IP Address(es) with Most Visits (").append(maxVisits).append(" times):\n");
        if (ipsWithMaxVisits.isEmpty()) sb.append("   (No visits recorded? Spooky! 👻)\n");
        else for (String ip : ipsWithMaxVisits) sb.append("   - ").append(ip).append("\n");
        sb.append("\n");
        sb.append("--- 📅 Daily Activity Pulse --- \n");
        sb.append("Total Days with Recorded Activity: ").append(dayToIPsMap.size()).append("\n");
        if (busiestDay != null) {
            int totalVisitsOnBusiestDay = dayToIPsMap.get(busiestDay) != null ? dayToIPsMap.get(busiestDay).size() : 0;
            sb.append("💥 Busiest Day Overall (Most Visits): ").append(busiestDay)
              .append(" (with ").append(totalVisitsOnBusiestDay).append(" total visits recorded)\n");
            sb.append("   --- Spotlight on ").append(busiestDay).append(" ---\n");
            if (!busiestDayFrequentIPs.isEmpty()) {
                sb.append("   🥇 Most Frequent Visitor(s) on ").append(busiestDay).append(" (").append(maxVisitsOnBusiestDay).append(" times that day):\n");
                 for(String ip : busiestDayFrequentIPs) sb.append("      - ").append(ip).append("\n");
            } else sb.append("   (No specific frequent visitors found for this day, or only single visits.)\n");
        } else sb.append("   (No daily activity found to determine a busiest day.)\n");
        sb.append("\n=============================================\n");
        sb.append("✨ Overall Summary Complete! Use controls below to query specific dates. ✨\n");

        summaryResultsTextArea.setText(sb.toString());
        summaryResultsTextArea.setCaretPosition(0);
    }

    // Populate the date selector dropdowns
    private void populateDateSelectors() {
        Date minDate = analyzer.getMinDate();
        Date maxDate = analyzer.getMaxDate();
        Calendar cal = Calendar.getInstance();

        int startYear = cal.get(Calendar.YEAR);
        int endYear = startYear;
        int defaultMonthIndex = 0;
        int defaultDay = 1;

        if (minDate != null) {
            cal.setTime(minDate);
            startYear = cal.get(Calendar.YEAR);
            defaultMonthIndex = cal.get(Calendar.MONTH);
            defaultDay = cal.get(Calendar.DAY_OF_MONTH);
        }
        if (maxDate != null) {
            cal.setTime(maxDate);
            endYear = cal.get(Calendar.YEAR);
        }

        yearCombo.removeAllItems();
        if (minDate != null || maxDate != null) { // Only populate if logs have dates
             for (int y = startYear; y <= endYear; y++) {
                yearCombo.addItem(y);
             }
             yearCombo.setSelectedItem(startYear); // Default to start year
        } else {
             // Handle case with no dates in log - maybe add current year?
             yearCombo.addItem(Calendar.getInstance().get(Calendar.YEAR));
             yearCombo.setSelectedIndex(0);
        }


        monthCombo.setSelectedIndex(defaultMonthIndex); // Default based on min date or Jan
        dayCombo.setSelectedItem(defaultDay);          // Default based on min date or 1
    }


    // Action performed when "Analyze Selected Date" button is clicked
    private void analyzeSelectedDate() {
        String monthStr = (String) monthCombo.getSelectedItem();
        Integer dayInt = (Integer) dayCombo.getSelectedItem(); // Can be null if combo is empty
        Integer yearInt = (Integer) yearCombo.getSelectedItem(); // Can be null if combo is empty

        // Basic validation
        if (monthStr == null || dayInt == null || yearInt == null) {
            selectedDateResultsTextArea.setText("Error: Please select a valid Month, Day, and Year.");
            return;
        }

        // Construct the "MMM dd" key format
        String dayKey = String.format("%s %02d", monthStr, dayInt);

        StringBuilder resultSb = new StringBuilder();
        resultSb.append("--- Analysis for ").append(monthStr).append(" ").append(dayInt).append(", ").append(yearInt).append(" ---\n\n");

        // 1. Get Unique IPs for the day
        ArrayList<String> uniqueIPs = analyzer.uniqueIPVisitsOnDay(dayKey); // Use existing method
        resultSb.append("Unique IP visits on ").append(dayKey).append(":\n");
        resultSb.append("--------------------------------------\n");
        resultSb.append("Count: ").append(uniqueIPs.size()).append("\n\n");
        if (uniqueIPs.isEmpty()) {
            resultSb.append("(No unique visits recorded for this specific day)\n");
        } else {
            for (String ip : uniqueIPs) {
                resultSb.append(ip).append("\n");
            }
        }
        resultSb.append("\n"); // Add spacing

        // 2. Get Most Frequent IPs for the day
        // Check if day exists in the map first (important!)
        if (this.dayToIPsMap != null && this.dayToIPsMap.containsKey(dayKey)) {
            ArrayList<String> mostFrequentIPs = analyzer.iPsWithMostVisitsOnDay(this.dayToIPsMap, dayKey);

             // Need the count for display
             int maxVisitsOnThisDay = 0;
             if (!mostFrequentIPs.isEmpty()) {
                 HashMap<String, Integer> countsOnDay = new HashMap<>();
                 ArrayList<String> ipsOnDay = this.dayToIPsMap.get(dayKey);
                 if(ipsOnDay != null) {
                     for (String ip : ipsOnDay) {
                         countsOnDay.put(ip, countsOnDay.getOrDefault(ip, 0) + 1);
                     }
                     maxVisitsOnThisDay = analyzer.mostNumberVisitsByIP(countsOnDay);
                 }
             }


            resultSb.append("Most Frequent IP(s) on ").append(dayKey).append(":\n");
            resultSb.append("--------------------------------------\n");
            if (mostFrequentIPs.isEmpty()) {
                resultSb.append("(No repeated visits on this day, or day had no visits)\n");
            } else {
                 resultSb.append("Visit Count: ").append(maxVisitsOnThisDay).append("\n");
                 resultSb.append("IP(s):\n");
                for (String ip : mostFrequentIPs) {
                    resultSb.append(" - ").append(ip).append("\n");
                }
            }
        } else {
            resultSb.append("Most Frequent IP(s) on ").append(dayKey).append(":\n");
            resultSb.append("--------------------------------------\n");
            resultSb.append("(No activity recorded for this day in the logs)\n");
        }


        selectedDateResultsTextArea.setText(resultSb.toString());
        selectedDateResultsTextArea.setCaretPosition(0); // Scroll to top
    }
}
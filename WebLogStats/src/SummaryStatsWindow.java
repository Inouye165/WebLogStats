// FILE: SummaryStatsWindow.java

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SummaryStatsWindow extends JFrame {

    private LogAnalyzer analyzer;
    private String shortFilename;

    private JLabel fileInfoLabel;
    private JTextArea resultsTextArea;

    public SummaryStatsWindow(LogAnalyzer analyzer, String filename) {
        this.analyzer = analyzer;
        this.shortFilename = filename;

        setTitle("📊 Overall Log Statistics Fun Zone! 🎉");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window, not the whole app
        setPreferredSize(new Dimension(700, 600)); // A bit wider/taller

        initComponents();
        populateResults(); // Calculate and display stats

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        // Top Panel for File Info
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        fileInfoLabel = new JLabel("Analyzing File: N/A");
        fileInfoLabel.setFont(fileInfoLabel.getFont().deriveFont(Font.BOLD, 14f));
        topPanel.add(fileInfoLabel);

        // Center Panel for Results
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        resultsTextArea = new JTextArea(25, 60); // More rows/cols
        resultsTextArea.setEditable(false);
        resultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13)); // Slightly larger monospaced font
        // Optional: Add line wrap if preferred, but Monospaced works well without it for tables
        // resultsTextArea.setLineWrap(true);
        // resultsTextArea.setWrapStyleWord(true);


        JScrollPane scrollPane = new JScrollPane(resultsTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);
    }

    // Method to perform calculations and format the output string
    private void populateResults() {
        fileInfoLabel.setText("Analyzing File: " + shortFilename);

        StringBuilder sb = new StringBuilder();
        sb.append("🚀 Welcome to the Log Stats Extravaganza! 🚀\n");
        sb.append("=============================================\n\n");

        // Perform calculations first
        HashMap<String, Integer> ipCountsMap = analyzer.countVisitsPerIP();
        int totalUniqueIPs = ipCountsMap.size(); // analyzer.countUniqueIPs() could also be used now
        int maxVisits = analyzer.mostNumberVisitsByIP(ipCountsMap);
        ArrayList<String> ipsWithMaxVisits = analyzer.iPsMostVisits(ipCountsMap);
        HashMap<String, ArrayList<String>> dayToIPsMap = analyzer.iPsForDays();
        String busiestDay = analyzer.dayWithMostIPVisits(dayToIPsMap);
        ArrayList<String> busiestDayFrequentIPs = new ArrayList<>(); // Initialize empty
        int maxVisitsOnBusiestDay = 0;

        // Only calculate busiest day stats if a busiest day was found
        if (busiestDay != null && dayToIPsMap.containsKey(busiestDay)) {
            busiestDayFrequentIPs = analyzer.iPsWithMostVisitsOnDay(dayToIPsMap, busiestDay);
             // We need the count for display, let's recalculate it for the busiest day's IPs
             HashMap<String, Integer> countsOnBusiestDay = new HashMap<>();
             ArrayList<String> ipsOnBusiest = dayToIPsMap.get(busiestDay);
             if (ipsOnBusiest != null) {
                for (String ip : ipsOnBusiest) {
                    countsOnBusiestDay.put(ip, countsOnBusiestDay.getOrDefault(ip, 0) + 1);
                }
                 maxVisitsOnBusiestDay = analyzer.mostNumberVisitsByIP(countsOnBusiestDay);
             }
        }


        // --- Format Output ---

        // --- IP Visit Counts Analysis ---
        sb.append("--- 📈 IP Visit Frequency Analysis --- \n");
        sb.append("Total Unique IP Addresses Found: ").append(totalUniqueIPs).append("\n");

        sb.append("🥇 Maximum Visits by a Single IP: ").append(maxVisits).append("\n");

        sb.append("🏆 IP Address(es) with Most Visits (").append(maxVisits).append(" times):\n");
        if (ipsWithMaxVisits.isEmpty()) {
            sb.append("   (No visits recorded? Spooky! 👻)\n");
        } else {
            for (String ip : ipsWithMaxVisits) {
                sb.append("   - ").append(ip).append("\n");
            }
        }
        sb.append("\n"); // Add spacing

        // --- Daily Activity Analysis ---
        sb.append("--- 📅 Daily Activity Pulse --- \n");
        sb.append("Total Days with Recorded Activity: ").append(dayToIPsMap.size()).append("\n");

        if (busiestDay != null) {
            int totalVisitsOnBusiestDay = dayToIPsMap.get(busiestDay) != null ? dayToIPsMap.get(busiestDay).size() : 0;
            sb.append("💥 Busiest Day Overall (Most Visits): ").append(busiestDay)
              .append(" (with ").append(totalVisitsOnBusiestDay).append(" total visits recorded)\n");

            // --- Analysis for the Busiest Day ---
            sb.append("   --- Spotlight on ").append(busiestDay).append(" ---\n");
            if (!busiestDayFrequentIPs.isEmpty()) {
                sb.append("   🥇 Most Frequent Visitor(s) on ").append(busiestDay).append(" (").append(maxVisitsOnBusiestDay).append(" times that day):\n");
                 for(String ip : busiestDayFrequentIPs) {
                     sb.append("      - ").append(ip).append("\n");
                 }
            } else {
                 sb.append("   (No specific frequent visitors found for this day, or only single visits.)\n");
            }

        } else {
            sb.append("   (No daily activity found to determine a busiest day.)\n");
        }

        sb.append("\n=============================================\n");
        sb.append("✨ Analysis Complete! Keep exploring! ✨\n");


        // Set the text and scroll to top
        resultsTextArea.setText(sb.toString());
        resultsTextArea.setCaretPosition(0);
    }
}
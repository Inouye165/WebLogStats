// FILE: VisitCountWindow.java

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map; // Import Map explicitly

public class VisitCountWindow extends JFrame {

    private String shortFilename;
    private HashMap<String, Integer> ipCounts;

    private JLabel fileInfoLabel;
    private JTextArea resultsTextArea;

    public VisitCountWindow(String filename, HashMap<String, Integer> counts) {
        this.shortFilename = filename;
        this.ipCounts = counts;

        setTitle("Website Visit Counts per IP");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setPreferredSize(new Dimension(600, 500));

        initComponents();
        populateResults();

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initComponents() {
        // Top Panel for File Info
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        fileInfoLabel = new JLabel("File: N/A");
        fileInfoLabel.setFont(fileInfoLabel.getFont().deriveFont(Font.BOLD));
        topPanel.add(fileInfoLabel);

        // Center Panel for Results
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        resultsTextArea = new JTextArea(20, 40);
        resultsTextArea.setEditable(false);
        resultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // Monospaced font for alignment

        JScrollPane scrollPane = new JScrollPane(resultsTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);
    }

    private void populateResults() {
        fileInfoLabel.setText("File: " + shortFilename);

        StringBuilder sb = new StringBuilder();
        sb.append("Total Unique IPs Found: ").append(ipCounts.size()).append("\n");
        sb.append("------------------------------------------\n");
        sb.append("IP Address          \tVisits\n"); // Header with tab
        sb.append("------------------------------------------\n");

        if (ipCounts.isEmpty()) {
            sb.append("(No log entries found or processed)");
        } else {
            // Sort by IP address for consistent display (optional but nice)
            ipCounts.entrySet().stream()
                  .sorted(Map.Entry.comparingByKey())
                  .forEach(entry -> {
                      // Format using String.format for potential alignment
                      sb.append(String.format("%-20s\t%d%n", entry.getKey(), entry.getValue()));
                      // Or simpler: sb.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
                  });
        }

        resultsTextArea.setText(sb.toString());
        resultsTextArea.setCaretPosition(0); // Scroll to the top
    }
}
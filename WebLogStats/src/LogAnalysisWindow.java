import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LogAnalysisWindow extends JFrame {

    private LogAnalyzer analyzer; // Reference to the analyzer with loaded data
    private String shortFilename;

    // UI Components
    private JLabel fileInfoLabel;
    private JLabel totalIPsLabel;
    private JComboBox<Integer> yearCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> dayCombo;
    private JButton analyzeDateButton;
    private JTextArea resultsTextArea; // To display the list of IPs

    public LogAnalysisWindow(LogAnalyzer logAnalyzer, String filename) {
        this.analyzer = logAnalyzer;
        this.shortFilename = filename;

        setTitle("Log Analysis Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit application when closed
        setPreferredSize(new Dimension(800, 650)); // Increased overall window size

        initComponents();     // Create and arrange components
        populateInitialData(); // Fill in file info, total IPs, and date ranges

        pack();               // Adjust window size based on components
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);     // Show the frame
    }

    private void initComponents() {
        // --- Top Panel (File Info & Total IPs) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        fileInfoLabel = new JLabel("File: N/A");
        totalIPsLabel = new JLabel("Total Unique IPs: N/A");
        totalIPsLabel.setFont(totalIPsLabel.getFont().deriveFont(Font.BOLD, 14f));

        topPanel.add(fileInfoLabel, BorderLayout.NORTH);
        topPanel.add(totalIPsLabel, BorderLayout.SOUTH);

        // --- Center Panel using BorderLayout ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // --- Date Selection Panel Reorganized with GridBagLayout ---
        JPanel dateSelectionPanel = new JPanel(new GridBagLayout());
        dateSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select Date for Analysis"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);  // Padding around components
        c.anchor = GridBagConstraints.WEST; // Left-align all components

        // --- Row 1: Explanation Label ---
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;  // Span across 4 columns
        c.fill = GridBagConstraints.HORIZONTAL;
        JLabel explanationLabel = new JLabel(
            "<html><body style='width: 300px; font-style: italic; color: gray;'>"
            + "Select a month, day, and year within the log file's range to see unique IP visits."
            + "</body></html>"
        );
        dateSelectionPanel.add(explanationLabel, c);

        // --- Row 2: Month and Day Selection ---
        c.gridwidth = 1;          // Reset to single column span
        c.fill = GridBagConstraints.NONE;  // Reset fill
        c.gridy = 1;

        // Column 0: "Month:" label
        c.gridx = 0;
        dateSelectionPanel.add(new JLabel("Month:"), c);

        // Column 1: Month combo box
        c.gridx = 1;
        monthCombo = new JComboBox<>(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                                  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
        dateSelectionPanel.add(monthCombo, c);

        // Column 2: "Day:" label
        c.gridx = 2;
        dateSelectionPanel.add(new JLabel("Day:"), c);

        // Column 3: Day combo box
        c.gridx = 3;
        dayCombo = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayCombo.addItem(i);
        }
        dateSelectionPanel.add(dayCombo, c);

        // --- Row 3: Year Selection and Analyze Button ---
        c.gridy = 2;
        // Column 0: "Year:" label
        c.gridx = 0;
        dateSelectionPanel.add(new JLabel("Year:"), c);

        // Columns 1 and 2: Year combo box (span 2 columns)
        c.gridx = 1;
        c.gridwidth = 2;
        yearCombo = new JComboBox<>();
        dateSelectionPanel.add(yearCombo, c);

        // Column 3: Analyze Selected Date Button
        c.gridx = 3;
        c.gridwidth = 1; // Reset
        analyzeDateButton = new JButton("Analyze Selected Date");
        dateSelectionPanel.add(analyzeDateButton, c);

        // --- Results Display Panel ---
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Results for Selected Date"));

        resultsTextArea = new JTextArea(15, 40);
        resultsTextArea.setEditable(false);
        resultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(resultsTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Add Panels to Center Panel ---
        centerPanel.add(dateSelectionPanel, BorderLayout.NORTH);
        centerPanel.add(resultsPanel, BorderLayout.CENTER);

        // --- Add Top and Center Panels to Frame ---
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // --- Add Action Listener for the Analyze Button ---
        analyzeDateButton.addActionListener(e -> analyzeSelectedDate());
    }

    private void populateInitialData() {
        // Display File Info
        fileInfoLabel.setText("File: " + shortFilename);

        // Display Total Unique IPs
        int totalIPs = analyzer.countUniqueIPs();
        totalIPsLabel.setText("Total Unique IPs: " + totalIPs);

        // Populate Date Combos based on analyzer's min/max dates
        Date minDate = analyzer.getMinDate();
        Date maxDate = analyzer.getMaxDate();
        Calendar cal = Calendar.getInstance();

        int startYear = cal.get(Calendar.YEAR); // Default
        int endYear = startYear;
        int defaultMonthIndex = 0; // Jan
        int defaultDayValue = 1;

        if (minDate != null) {
            cal.setTime(minDate);
            startYear = cal.get(Calendar.YEAR);
            defaultMonthIndex = cal.get(Calendar.MONTH);
            defaultDayValue = cal.get(Calendar.DAY_OF_MONTH);
        }
        if (maxDate != null) {
            cal.setTime(maxDate);
            endYear = cal.get(Calendar.YEAR);
        }

        // Populate Year Combo
        yearCombo.removeAllItems(); // Clear defaults if any
        if (startYear <= endYear) {
            for (int y = startYear; y <= endYear; y++) {
                yearCombo.addItem(y);
            }
            if (minDate != null) {
                cal.setTime(minDate);
                yearCombo.setSelectedItem(cal.get(Calendar.YEAR));
            }
        } else {
            yearCombo.addItem(startYear);
            if (minDate != null) {
                cal.setTime(minDate);
                yearCombo.setSelectedItem(cal.get(Calendar.YEAR));
            }
        }

        // Set default month/day selection
        monthCombo.setSelectedIndex(defaultMonthIndex);
        dayCombo.setSelectedItem(defaultDayValue);

        // Initial results text
        resultsTextArea.setText("Select a date and click 'Analyze Selected Date'.");
    }

    // Method called when the button is clicked
    private void analyzeSelectedDate() {
        String selectedMonth = (String) monthCombo.getSelectedItem(); // e.g., "Sep"
        int selectedDay = (Integer) dayCombo.getSelectedItem();         // e.g., 14
        Integer selectedYear = (Integer) yearCombo.getSelectedItem();     // e.g., 2015

        if (selectedYear == null || selectedMonth == null || selectedDay == 0) {
            resultsTextArea.setText("Error: Invalid date selection.");
            return;
        }

        // Format day with leading zero (e.g., "05", "14")
        String formattedDay = String.format("%02d", selectedDay);
        // Format date string for LogAnalyzer (e.g., "Sep 14")
        String dateForAnalysis = selectedMonth + " " + formattedDay;

        // Provide feedback in the results area
        resultsTextArea.setText("Analyzing " + dateForAnalysis + "...");
        ArrayList<String> ipsOnDay = analyzer.uniqueIPVisitsOnDay(dateForAnalysis);

        // Build and display results
        StringBuilder resultText = new StringBuilder();
        resultText.append("Unique IP visits on " + dateForAnalysis + ", " + selectedYear + ":\n");
        resultText.append("-------------------------------------------\n");
        resultText.append("Count: " + ipsOnDay.size() + "\n\n");

        if (ipsOnDay.isEmpty()) {
            resultText.append("(No visits recorded for this date)");
        } else {
            for (String ip : ipsOnDay) {
                resultText.append(ip).append("\n");
            }
        }

        resultsTextArea.setText(resultText.toString());
        resultsTextArea.setCaretPosition(0); // Scroll back to the top
    }
}

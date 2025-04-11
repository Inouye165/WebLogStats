// FILE: LogAnalysisWindow.java

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar; // Needed for setting Date objects correctly
import java.util.Date;
import java.util.Map; // For month name to number mapping
import java.util.HashMap; // For month name to number mapping


public class LogAnalysisWindow extends JFrame {

    private LogAnalyzer analyzer;
    private String shortFilename;
    private String initialRangeResults;
    // private String initialPredefinedTestResults; // Removed previously

    // UI Components
    private JLabel fileInfoLabel;
    private JLabel totalIPsLabel;
    // Start Date Components
    private JComboBox<Integer> startYearCombo;
    private JComboBox<String> startMonthCombo;
    private JComboBox<Integer> startDayCombo;
    // End Date Components
    private JComboBox<Integer> endYearCombo;
    private JComboBox<String> endMonthCombo;
    private JComboBox<Integer> endDayCombo;

    private JButton analyzeDateRangeButton; // Renamed button
    private JTextArea dateResultsTextArea;

    private JSpinner numSpinner;
    private JButton analyzeStatusButton;
    private JTextArea otherResultsTextArea;

    // Map month names to Calendar month constants (0-11)
    private static final Map<String, Integer> MONTH_MAP = createMonthMap();

    // Constructor (Signature unchanged from last working version)
    public LogAnalysisWindow(LogAnalyzer logAnalyzer, String filename, String rangeResults) {
        this.analyzer = logAnalyzer;
        this.shortFilename = filename;
        this.initialRangeResults = rangeResults;

        setTitle("Log Analysis Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 750)); // Adjust size if needed

        initComponents();
        populateInitialData();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Helper to create month map
    private static Map<String, Integer> createMonthMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Jan", Calendar.JANUARY); map.put("Feb", Calendar.FEBRUARY);
        map.put("Mar", Calendar.MARCH);   map.put("Apr", Calendar.APRIL);
        map.put("May", Calendar.MAY);     map.put("Jun", Calendar.JUNE);
        map.put("Jul", Calendar.JULY);    map.put("Aug", Calendar.AUGUST);
        map.put("Sep", Calendar.SEPTEMBER); map.put("Oct", Calendar.OCTOBER);
        map.put("Nov", Calendar.NOVEMBER); map.put("Dec", Calendar.DECEMBER);
        return map;
    }

    private void initComponents() {
        // --- Top Panel --- (No change)
        JPanel topPanel = new JPanel(new BorderLayout());
        // ... (fileInfoLabel, totalIPsLabel setup) ...
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); fileInfoLabel = new JLabel("File: N/A"); totalIPsLabel = new JLabel("Total Unique IPs: N/A"); totalIPsLabel.setFont(totalIPsLabel.getFont().deriveFont(Font.BOLD, 14f)); topPanel.add(fileInfoLabel, BorderLayout.NORTH); topPanel.add(totalIPsLabel, BorderLayout.SOUTH);

        // --- Center Panel using BoxLayout --- (No change)
        JPanel centerPanel = new JPanel(); centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // --- Date SELECTION Panel UPDATED for Range ---
        JPanel dateSelectionPanel = new JPanel(new GridBagLayout());
        dateSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select Date Range for Analysis"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 5, 2, 5); // Adjusted padding
        c.anchor = GridBagConstraints.WEST;

        // --- Row 0: Explanation ---
        c.gridx = 0; c.gridy = 0; c.gridwidth = 6; c.fill = GridBagConstraints.HORIZONTAL;
        JLabel explanationLabel = new JLabel("<html><body style='width: 400px;'>Select start and end dates (inclusive). Set both to the same date for single-day analysis.</body></html>");
        dateSelectionPanel.add(explanationLabel, c);
        c.gridwidth = 1; c.fill = GridBagConstraints.NONE; // Reset defaults

        // --- Row 1: Start Date Controls ---
        c.gridy = 1;
        c.gridx = 0; dateSelectionPanel.add(new JLabel("Start Date:"), c);
        c.gridx = 1; startMonthCombo = new JComboBox<>(MONTH_MAP.keySet().toArray(new String[0])); dateSelectionPanel.add(startMonthCombo, c);
        c.gridx = 2; dateSelectionPanel.add(new JLabel("Day:"), c);
        c.gridx = 3; startDayCombo = new JComboBox<>(); for(int i=1; i<=31; i++) startDayCombo.addItem(i); dateSelectionPanel.add(startDayCombo, c);
        c.gridx = 4; dateSelectionPanel.add(new JLabel("Year:"), c);
        c.gridx = 5; startYearCombo = new JComboBox<>(); dateSelectionPanel.add(startYearCombo, c);

        // --- Row 2: End Date Controls ---
        c.gridy = 2;
        c.gridx = 0; dateSelectionPanel.add(new JLabel("End Date:"), c);
        c.gridx = 1; endMonthCombo = new JComboBox<>(MONTH_MAP.keySet().toArray(new String[0])); dateSelectionPanel.add(endMonthCombo, c);
        c.gridx = 2; dateSelectionPanel.add(new JLabel("Day:"), c);
        c.gridx = 3; endDayCombo = new JComboBox<>(); for(int i=1; i<=31; i++) endDayCombo.addItem(i); dateSelectionPanel.add(endDayCombo, c);
        c.gridx = 4; dateSelectionPanel.add(new JLabel("Year:"), c);
        c.gridx = 5; endYearCombo = new JComboBox<>(); dateSelectionPanel.add(endYearCombo, c);

        // --- Row 3: Analyze Button ---
        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 6; // Span button across
        c.anchor = GridBagConstraints.CENTER; // Center button
        analyzeDateRangeButton = new JButton("Analyze Selected Date Range"); // Renamed button
        dateSelectionPanel.add(analyzeDateRangeButton, c);
        // --- End Date Selection Panel ---


        // --- Date Results Display Panel --- (No change, title updated later)
        JPanel dateResultsPanel = new JPanel(new BorderLayout());
        dateResultsPanel.setBorder(BorderFactory.createTitledBorder("Results for Selected Date Range")); // Updated title
        dateResultsTextArea = new JTextArea(10, 40); dateResultsTextArea.setEditable(false); dateResultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); JScrollPane dateScrollPane = new JScrollPane(dateResultsTextArea); dateScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); dateScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); dateResultsPanel.add(dateScrollPane, BorderLayout.CENTER);

        // --- Other Results Panel (Status Input & Results) --- (No change)
        JPanel otherResultsPanel = new JPanel(new BorderLayout(0, 5));
        // ... (Setup as before: numInputPanel with spinner/button, otherResultsTextArea in scrollpane) ...
        otherResultsPanel.setBorder(BorderFactory.createTitledBorder("Other Analysis Results")); JPanel numInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); JLabel numLabel = new JLabel("Show entries with Status Code > "); SpinnerNumberModel spinnerModel = new SpinnerNumberModel(400, 0, 999, 1); numSpinner = new JSpinner(spinnerModel); numSpinner.setPreferredSize(new Dimension(60, numSpinner.getPreferredSize().height)); analyzeStatusButton = new JButton("Show Status Results"); numInputPanel.add(numLabel); numInputPanel.add(numSpinner); numInputPanel.add(analyzeStatusButton); otherResultsTextArea = new JTextArea(10, 40); otherResultsTextArea.setEditable(false); otherResultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); JScrollPane otherScrollPane = new JScrollPane(otherResultsTextArea); otherScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); otherScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); otherResultsPanel.add(numInputPanel, BorderLayout.NORTH); otherResultsPanel.add(otherScrollPane, BorderLayout.CENTER);


        // --- Add Panels VERTICALLY to centerPanel ---
        centerPanel.add(dateSelectionPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(dateResultsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(otherResultsPanel);
        // Removed predefined test panel section

        // --- Add Top and Center Panels to Frame ---
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // --- Add Action Listeners ---
        analyzeDateRangeButton.addActionListener(e -> analyzeDateRange()); // Connect to new method name
        analyzeStatusButton.addActionListener(e -> analyzeStatusCode());
    }

    private void populateInitialData() {
        // Display File Info & Total IPs (No change)
        fileInfoLabel.setText("File: " + shortFilename);
        int totalIPs = analyzer.countUniqueIPs();
        totalIPsLabel.setText("Total Unique IPs: " + totalIPs);

        // --- Populate BOTH Date Combo Sets ---
        Date minDate = analyzer.getMinDate();
        Date maxDate = analyzer.getMaxDate();
        Calendar cal = Calendar.getInstance();

        // Defaults if no dates found
        int startYear = cal.get(Calendar.YEAR); int endYear = startYear;
        int defaultStartMonthIndex = 0; int defaultStartDay = 1;
        int defaultEndMonthIndex = 0; int defaultEndDay = 1;

        if (minDate != null) {
            cal.setTime(minDate);
            startYear = cal.get(Calendar.YEAR);
            defaultStartMonthIndex = cal.get(Calendar.MONTH);
            defaultStartDay = cal.get(Calendar.DAY_OF_MONTH);
        }
        if (maxDate != null) {
            cal.setTime(maxDate);
            endYear = cal.get(Calendar.YEAR);
            defaultEndMonthIndex = cal.get(Calendar.MONTH);
            defaultEndDay = cal.get(Calendar.DAY_OF_MONTH);
        } else if (minDate != null) { // If only min date, default end date to min date
             endYear = startYear;
             defaultEndMonthIndex = defaultStartMonthIndex;
             defaultEndDay = defaultStartDay;
        }

        // Populate Year Combos
        startYearCombo.removeAllItems();
        endYearCombo.removeAllItems();
        if (startYear <= endYear) {
            for (int y = startYear; y <= endYear; y++) {
                startYearCombo.addItem(y);
                endYearCombo.addItem(y);
            }
            // Set defaults
            startYearCombo.setSelectedItem(startYear);
            endYearCombo.setSelectedItem(endYear);
        } else { // Handle single year case or error
            startYearCombo.addItem(startYear);
            endYearCombo.addItem(startYear);
            startYearCombo.setSelectedItem(startYear);
            endYearCombo.setSelectedItem(startYear);
        }

        // Set default month/day selection
        startMonthCombo.setSelectedIndex(defaultStartMonthIndex);
        startDayCombo.setSelectedItem(defaultStartDay);
        endMonthCombo.setSelectedIndex(defaultEndMonthIndex);
        endDayCombo.setSelectedItem(defaultEndDay);
        // --- End Date Combo Population ---

        // Initial text for DATE results area
        dateResultsTextArea.setText("Select a date range and click 'Analyze Selected Date Range'.");
        dateResultsTextArea.setCaretPosition(0);

        // Initial text for OTHER results Area
        otherResultsTextArea.setText(initialRangeResults + "\n\n"
                                   + "Enter status code threshold above and click 'Show Status Results'.");
        otherResultsTextArea.setCaretPosition(0);
    }

    // --- Renamed and UPDATED method to handle date range analysis ---
    private void analyzeDateRange() {
        // Get selected values
        String startMonthStr = (String) startMonthCombo.getSelectedItem();
        int startDayInt = (Integer) startDayCombo.getSelectedItem();
        Integer startYearInt = (Integer) startYearCombo.getSelectedItem();

        String endMonthStr = (String) endMonthCombo.getSelectedItem();
        int endDayInt = (Integer) endDayCombo.getSelectedItem();
        Integer endYearInt = (Integer) endYearCombo.getSelectedItem();

        if (startYearInt == null || endYearInt == null) {
            dateResultsTextArea.setText("Error: Please select valid years.");
            return;
        }

        // Convert selections to Date objects spanning the whole day
        Date startDate = createDate(startYearInt, MONTH_MAP.get(startMonthStr), startDayInt, true); // Start of day
        Date endDate = createDate(endYearInt, MONTH_MAP.get(endMonthStr), endDayInt, false); // End of day

        if (startDate == null || endDate == null) {
            dateResultsTextArea.setText("Error: Could not parse selected dates.");
            return;
        }

        if (startDate.after(endDate)) {
             dateResultsTextArea.setText("Error: Start date cannot be after end date.");
             return;
        }

        // Format display strings for the range
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy");
        String rangeDisplay = displayFormat.format(startDate) + " to " + displayFormat.format(endDate);

        // Perform analysis
        dateResultsTextArea.setText("Analyzing range: " + rangeDisplay + "...");
        if (analyzer == null) { dateResultsTextArea.setText("Error: LogAnalyzer not initialized."); return; }

        ArrayList<String> ipsInRange = analyzer.getUniqueIPsForDateRange(startDate, endDate);

        // Build and display results
        StringBuilder resultText = new StringBuilder();
        resultText.append("Unique IP visits from " + rangeDisplay + ":\n");
        resultText.append("-------------------------------------------\n");
        resultText.append("Count: " + ipsInRange.size() + "\n\n");
        if (ipsInRange.isEmpty()) {
            resultText.append("(No visits recorded for this date range)");
        } else {
            for (String ip : ipsInRange) {
                resultText.append(ip).append("\n");
            }
        }
        dateResultsTextArea.setText(resultText.toString());
        dateResultsTextArea.setCaretPosition(0); // Scroll back to the top
    }

    // Helper method to create a Date object from Y/M/D, setting time to start or end of day
    private Date createDate(int year, int month, int day, boolean startOfDay) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setLenient(false); // Make date validation stricter
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month); // Calendar months are 0-based
            cal.set(Calendar.DAY_OF_MONTH, day);

            if (startOfDay) {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            } else { // End of day
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
            }
            // This will throw an exception if the date is invalid (e.g., Feb 30)
            // due to setLenient(false)
            return cal.getTime();
        } catch (Exception e) {
            System.err.println("Error creating date: " + year + "/" + (month+1) + "/" + day + " - " + e.getMessage());
            return null; // Indicate error
        }
    }

    // analyzeStatusCode method remains the same
    private void analyzeStatusCode() { /* ... no change ... */ }

} // End Class
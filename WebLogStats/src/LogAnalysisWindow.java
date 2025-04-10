// FILE: LogAnalysisWindow.java

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LogAnalysisWindow extends JFrame {

    private LogAnalyzer analyzer;
    private String shortFilename;
    private String initialRangeResults;
    // Field for predefined test results
    private String initialPredefinedTestResults;

    // UI Components
    private JLabel fileInfoLabel;
    private JLabel totalIPsLabel;
    private JComboBox<Integer> yearCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> dayCombo;
    private JButton analyzeDateButton;
    private JTextArea dateResultsTextArea;
    private JSpinner numSpinner;
    private JButton analyzeStatusButton;
    private JTextArea otherResultsTextArea;
    // Component for predefined test results
    private JTextArea predefinedTestTextArea;

    // Constructor accepting 4 arguments
    public LogAnalysisWindow(LogAnalyzer logAnalyzer, String filename,
                             String rangeResults, String predefinedTestResults) { // <<< Has 4 arguments
        this.analyzer = logAnalyzer;
        this.shortFilename = filename;
        this.initialRangeResults = rangeResults;
        this.initialPredefinedTestResults = predefinedTestResults; // Store predefined results

        setTitle("Log Analysis Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 850)); // Needs more height

        initComponents();
        populateInitialData();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        // --- Top Panel --- (No change)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); fileInfoLabel = new JLabel("File: N/A"); totalIPsLabel = new JLabel("Total Unique IPs: N/A"); totalIPsLabel.setFont(totalIPsLabel.getFont().deriveFont(Font.BOLD, 14f)); topPanel.add(fileInfoLabel, BorderLayout.NORTH); topPanel.add(totalIPsLabel, BorderLayout.SOUTH);

        // --- Center Panel using BoxLayout --- (No change)
        JPanel centerPanel = new JPanel(); centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // --- Date Selection Panel (GridBagLayout) --- (Using your provided code)
        JPanel dateSelectionPanel = new JPanel(new GridBagLayout()); dateSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select Date for Analysis")); GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(5, 5, 5, 5); c.anchor = GridBagConstraints.WEST; c.gridx = 0; c.gridy = 0; c.gridwidth = 4; c.fill = GridBagConstraints.HORIZONTAL; JLabel explanationLabel = new JLabel("<html><body style='width: 300px; font-style: italic; color: gray;'>Select a month...</body></html>"); dateSelectionPanel.add(explanationLabel, c); c.gridwidth = 1; c.fill = GridBagConstraints.NONE; c.gridy = 1; c.gridx = 0; dateSelectionPanel.add(new JLabel("Month:"), c); c.gridx = 1; monthCombo = new JComboBox<>(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}); dateSelectionPanel.add(monthCombo, c); c.gridx = 2; dateSelectionPanel.add(new JLabel("Day:"), c); c.gridx = 3; dayCombo = new JComboBox<>(); for (int i = 1; i <= 31; i++) dayCombo.addItem(i); dateSelectionPanel.add(dayCombo, c); c.gridy = 2; c.gridx = 0; dateSelectionPanel.add(new JLabel("Year:"), c); c.gridx = 1; c.gridwidth = 2; yearCombo = new JComboBox<>(); dateSelectionPanel.add(yearCombo, c); c.gridx = 3; c.gridwidth = 1; analyzeDateButton = new JButton("Analyze Selected Date"); dateSelectionPanel.add(analyzeDateButton, c);

        // --- Date Results Display Panel --- (No change)
        JPanel dateResultsPanel = new JPanel(new BorderLayout()); dateResultsPanel.setBorder(BorderFactory.createTitledBorder("Results for Selected Date")); dateResultsTextArea = new JTextArea(10, 40); dateResultsTextArea.setEditable(false); dateResultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); JScrollPane dateScrollPane = new JScrollPane(dateResultsTextArea); dateScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); dateScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); dateResultsPanel.add(dateScrollPane, BorderLayout.CENTER);

        // --- Other Results Panel (Status Input & Results) --- (No change)
        JPanel otherResultsPanel = new JPanel(new BorderLayout(0, 5)); otherResultsPanel.setBorder(BorderFactory.createTitledBorder("Other Analysis Results")); JPanel numInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); JLabel numLabel = new JLabel("Show entries with Status Code > "); SpinnerNumberModel spinnerModel = new SpinnerNumberModel(400, 0, 999, 1); numSpinner = new JSpinner(spinnerModel); numSpinner.setPreferredSize(new Dimension(60, numSpinner.getPreferredSize().height)); analyzeStatusButton = new JButton("Show Status Results"); numInputPanel.add(numLabel); numInputPanel.add(numSpinner); numInputPanel.add(analyzeStatusButton); otherResultsTextArea = new JTextArea(10, 40); otherResultsTextArea.setEditable(false); otherResultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); JScrollPane otherScrollPane = new JScrollPane(otherResultsTextArea); otherScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); otherScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); otherResultsPanel.add(numInputPanel, BorderLayout.NORTH); otherResultsPanel.add(otherScrollPane, BorderLayout.CENTER);

        // --- Predefined Assignment Test Results Panel --- (This was removed in later version, but present here)
        JPanel predefinedTestPanel = new JPanel(new BorderLayout());
        predefinedTestPanel.setBorder(BorderFactory.createTitledBorder("Assignment Test Results (weblog-short_log)"));
        predefinedTestTextArea = new JTextArea(5, 40); // Adjust rows as needed
        predefinedTestTextArea.setEditable(false);
        predefinedTestTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        predefinedTestTextArea.setForeground(Color.DARK_GRAY);
        JScrollPane predefinedScrollPane = new JScrollPane(predefinedTestTextArea);
        predefinedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        predefinedTestPanel.add(predefinedScrollPane, BorderLayout.CENTER);

        // --- Add Panels VERTICALLY to centerPanel ---
        centerPanel.add(dateSelectionPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(dateResultsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(otherResultsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing
        centerPanel.add(predefinedTestPanel); // Add the predefined panel

        // --- Add Top and Center Panels to Frame ---
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // --- Add Action Listeners ---
        analyzeDateButton.addActionListener(e -> analyzeSelectedDate());
        analyzeStatusButton.addActionListener(e -> analyzeStatusCode());
    }

    private void populateInitialData() {
        // Display File Info & Total IPs
        fileInfoLabel.setText("File: " + shortFilename);
        int totalIPs = analyzer.countUniqueIPs();
        totalIPsLabel.setText("Total Unique IPs: " + totalIPs);

        // Populate Date Combos
        Date minDate = analyzer.getMinDate(); Date maxDate = analyzer.getMaxDate(); Calendar cal = Calendar.getInstance(); int startYear = cal.get(Calendar.YEAR); int endYear = startYear; int defaultMonthIndex = 0; int defaultDayValue = 1; if (minDate != null) { cal.setTime(minDate); startYear = cal.get(Calendar.YEAR); defaultMonthIndex = cal.get(Calendar.MONTH); defaultDayValue = cal.get(Calendar.DAY_OF_MONTH); } if (maxDate != null) { cal.setTime(maxDate); endYear = cal.get(Calendar.YEAR); } yearCombo.removeAllItems(); if (startYear <= endYear) { for (int y = startYear; y <= endYear; y++) yearCombo.addItem(y); if (minDate != null) { cal.setTime(minDate); yearCombo.setSelectedItem(cal.get(Calendar.YEAR)); } } else { yearCombo.addItem(startYear); if (minDate != null) { cal.setTime(minDate); yearCombo.setSelectedItem(cal.get(Calendar.YEAR)); } } monthCombo.setSelectedIndex(defaultMonthIndex); dayCombo.setSelectedItem(defaultDayValue);

        // Initial text for DATE results area
        dateResultsTextArea.setText("Select a date and click 'Analyze Selected Date'.");
        dateResultsTextArea.setCaretPosition(0);

        // Initial text for OTHER results Area
        otherResultsTextArea.setText(initialRangeResults + "\n\n" + "Enter status code threshold above and click 'Show Status Results'.");
        otherResultsTextArea.setCaretPosition(0);

        // Populate Predefined Test Results Area
        predefinedTestTextArea.setText(initialPredefinedTestResults);
        predefinedTestTextArea.setCaretPosition(0);
    }

    // Method called when the date button is clicked
    private void analyzeSelectedDate() {
        String selectedMonth = (String) monthCombo.getSelectedItem(); int selectedDay = (Integer) dayCombo.getSelectedItem(); Integer selectedYear = (Integer) yearCombo.getSelectedItem(); if (selectedYear == null || selectedMonth == null || dayCombo.getSelectedItem() == null) { dateResultsTextArea.setText("Error: Invalid date selection."); return; } String formattedDay = String.format("%02d", selectedDay); String dateForAnalysis = selectedMonth + " " + formattedDay; dateResultsTextArea.setText("Analyzing " + dateForAnalysis + "..."); if (analyzer == null) { dateResultsTextArea.setText("Error: LogAnalyzer not initialized."); return; } ArrayList<String> ipsOnDay = analyzer.uniqueIPVisitsOnDay(dateForAnalysis); StringBuilder resultText = new StringBuilder(); resultText.append("Unique IP visits on " + dateForAnalysis + ", " + selectedYear + ":\n"); resultText.append("-------------------------------------------\n"); resultText.append("Count: " + ipsOnDay.size() + "\n\n"); if (ipsOnDay.isEmpty()) { resultText.append("(No visits recorded for this date)"); } else { for (String ip : ipsOnDay) resultText.append(ip).append("\n"); } dateResultsTextArea.setText(resultText.toString()); dateResultsTextArea.setCaretPosition(0);
    }

    // Method called when the status button is clicked
    private void analyzeStatusCode() {
        int statusCodeThreshold = (Integer) numSpinner.getValue(); otherResultsTextArea.setText("Analyzing status codes > " + statusCodeThreshold + "..."); if (analyzer == null) { otherResultsTextArea.setText("Error: LogAnalyzer not initialized."); return; } String statusOutput = analyzer.getAllHigherThanNum(statusCodeThreshold); StringBuilder finalText = new StringBuilder(); finalText.append(initialRangeResults); finalText.append("\n\n"); finalText.append(statusOutput); otherResultsTextArea.setText(finalText.toString()); otherResultsTextArea.setCaretPosition(0);
    }

} // End Class
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogAnalysisWindow extends JFrame {

    private LogAnalyzer analyzer;
    private String shortFilename;
    private String initialRangeResults;

    private JLabel fileInfoLabel;
    private JLabel totalIPsLabel;

    private JComboBox<Integer> startYearCombo;
    private JComboBox<String> startMonthCombo;
    private JComboBox<Integer> startDayCombo;

    private JComboBox<Integer> endYearCombo;
    private JComboBox<String> endMonthCombo;
    private JComboBox<Integer> endDayCombo;

    private JButton analyzeDateRangeButton;
    private JButton analyzeSingleDayButton;

    private JTextArea dateResultsTextArea;

    private JSpinner numSpinner;
    private JButton analyzeStatusButton;
    private JTextArea otherResultsTextArea;

    private static final Map<String, Integer> MONTH_MAP = createMonthMap();

    public LogAnalysisWindow(LogAnalyzer logAnalyzer, String filename, String rangeResults) {
        this.analyzer = logAnalyzer;
        this.shortFilename = filename;
        this.initialRangeResults = rangeResults;

        setTitle("Log Analysis Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 750));

        initComponents();
        populateInitialData();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

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
        JPanel topPanel = new JPanel(new BorderLayout());
        fileInfoLabel = new JLabel("File: N/A");
        totalIPsLabel = new JLabel("Total Unique IPs: N/A");
        totalIPsLabel.setFont(totalIPsLabel.getFont().deriveFont(Font.BOLD, 14f));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(fileInfoLabel, BorderLayout.NORTH);
        topPanel.add(totalIPsLabel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JPanel dateSelectionPanel = new JPanel(new GridBagLayout());
        dateSelectionPanel.setBorder(BorderFactory.createTitledBorder("Select Date Range for Analysis"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 5, 2, 5);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0; c.gridwidth = 6;
        JLabel explanationLabel = new JLabel("<html><body style='width: 400px;'>Select start and end dates (inclusive). Set both to the same date for single-day analysis.</body></html>");
        dateSelectionPanel.add(explanationLabel, c);
        c.gridwidth = 1;

        c.gridy = 1;
        c.gridx = 0; dateSelectionPanel.add(new JLabel("Start Date:"), c);
        c.gridx = 1; startMonthCombo = new JComboBox<>(MONTH_MAP.keySet().toArray(new String[0])); dateSelectionPanel.add(startMonthCombo, c);
        c.gridx = 2; dateSelectionPanel.add(new JLabel("Day:"), c);
        c.gridx = 3; startDayCombo = new JComboBox<>(); for(int i=1; i<=31; i++) startDayCombo.addItem(i); dateSelectionPanel.add(startDayCombo, c);
        c.gridx = 4; dateSelectionPanel.add(new JLabel("Year:"), c);
        c.gridx = 5; startYearCombo = new JComboBox<>(); dateSelectionPanel.add(startYearCombo, c);

        c.gridy = 2;
        c.gridx = 0; dateSelectionPanel.add(new JLabel("End Date:"), c);
        c.gridx = 1; endMonthCombo = new JComboBox<>(MONTH_MAP.keySet().toArray(new String[0])); dateSelectionPanel.add(endMonthCombo, c);
        c.gridx = 2; dateSelectionPanel.add(new JLabel("Day:"), c);
        c.gridx = 3; endDayCombo = new JComboBox<>(); for(int i=1; i<=31; i++) endDayCombo.addItem(i); dateSelectionPanel.add(endDayCombo, c);
        c.gridx = 4; dateSelectionPanel.add(new JLabel("Year:"), c);
        c.gridx = 5; endYearCombo = new JComboBox<>(); dateSelectionPanel.add(endYearCombo, c);

        c.gridy = 3; c.gridx = 0; c.gridwidth = 6; c.anchor = GridBagConstraints.CENTER;
        analyzeDateRangeButton = new JButton("Analyze Selected Date Range");
        dateSelectionPanel.add(analyzeDateRangeButton, c);

        c.gridy = 4;
        analyzeSingleDayButton = new JButton("Analyze Single Day");
        dateSelectionPanel.add(analyzeSingleDayButton, c);

        JPanel dateResultsPanel = new JPanel(new BorderLayout());
        dateResultsPanel.setBorder(BorderFactory.createTitledBorder("Results for Selected Date Range"));
        dateResultsTextArea = new JTextArea(10, 40);
        dateResultsTextArea.setEditable(false);
        dateResultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane dateScrollPane = new JScrollPane(dateResultsTextArea);
        dateScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dateScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dateResultsPanel.add(dateScrollPane, BorderLayout.CENTER);

        JPanel otherResultsPanel = new JPanel(new BorderLayout(0, 5));
        otherResultsPanel.setBorder(BorderFactory.createTitledBorder("Other Analysis Results"));
        JPanel numInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel numLabel = new JLabel("Show entries with Status Code > ");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(400, 0, 999, 1);
        numSpinner = new JSpinner(spinnerModel);
        numSpinner.setPreferredSize(new Dimension(60, numSpinner.getPreferredSize().height));
        analyzeStatusButton = new JButton("Show Status Results");
        numInputPanel.add(numLabel); numInputPanel.add(numSpinner); numInputPanel.add(analyzeStatusButton);
        otherResultsTextArea = new JTextArea(10, 40);
        otherResultsTextArea.setEditable(false);
        otherResultsTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane otherScrollPane = new JScrollPane(otherResultsTextArea);
        otherScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        otherScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        otherResultsPanel.add(numInputPanel, BorderLayout.NORTH);
        otherResultsPanel.add(otherScrollPane, BorderLayout.CENTER);

        centerPanel.add(dateSelectionPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(dateResultsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(otherResultsPanel);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        analyzeDateRangeButton.addActionListener(e -> analyzeDateRange());
        analyzeSingleDayButton.addActionListener(e -> analyzeSingleDay());
        analyzeStatusButton.addActionListener(e -> analyzeStatusCode());
    }

    private void populateInitialData() {
        fileInfoLabel.setText("File: " + shortFilename);
        int totalIPs = analyzer.countUniqueIPs();
        totalIPsLabel.setText("Total Unique IPs: " + totalIPs);

        Date minDate = analyzer.getMinDate();
        Date maxDate = analyzer.getMaxDate();
        Calendar cal = Calendar.getInstance();

        int startYear = cal.get(Calendar.YEAR), endYear = startYear;
        int defaultStartMonthIndex = 0, defaultStartDay = 1;
        int defaultEndMonthIndex = 0, defaultEndDay = 1;

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
        } else if (minDate != null) {
            endYear = startYear;
            defaultEndMonthIndex = defaultStartMonthIndex;
            defaultEndDay = defaultStartDay;
        }

        startYearCombo.removeAllItems(); endYearCombo.removeAllItems();
        for (int y = startYear; y <= endYear; y++) {
            startYearCombo.addItem(y); endYearCombo.addItem(y);
        }
        startYearCombo.setSelectedItem(startYear); endYearCombo.setSelectedItem(endYear);
        startMonthCombo.setSelectedIndex(defaultStartMonthIndex);
        startDayCombo.setSelectedItem(defaultStartDay);
        endMonthCombo.setSelectedIndex(defaultEndMonthIndex);
        endDayCombo.setSelectedItem(defaultEndDay);

        dateResultsTextArea.setText("Select a date range or a single day, then click 'Analyze'.");
        dateResultsTextArea.setCaretPosition(0);

        otherResultsTextArea.setText(initialRangeResults + "\n\nEnter a status code and click 'Show Status Results'.");
        otherResultsTextArea.setCaretPosition(0);
    }

    private void analyzeDateRange() {
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

        Date startDate = createDate(startYearInt, MONTH_MAP.get(startMonthStr), startDayInt, true);
        Date endDate = createDate(endYearInt, MONTH_MAP.get(endMonthStr), endDayInt, false);

        if (startDate == null || endDate == null || startDate.after(endDate)) {
            dateResultsTextArea.setText("Invalid date range.");
            return;
        }

        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy");
        String rangeDisplay = displayFormat.format(startDate) + " to " + displayFormat.format(endDate);

        ArrayList<String> ipsInRange = analyzer.getUniqueIPsForDateRange(startDate, endDate);

        StringBuilder resultText = new StringBuilder();
        resultText.append("Unique IP visits from ").append(rangeDisplay).append(":\n");
        resultText.append("-------------------------------------------\n");
        resultText.append("Count: ").append(ipsInRange.size()).append("\n\n");
        if (ipsInRange.isEmpty()) {
            resultText.append("(No visits recorded for this date range)");
        } else {
            for (String ip : ipsInRange) {
                resultText.append(ip).append("\n");
            }
        }

        dateResultsTextArea.setText(resultText.toString());
        dateResultsTextArea.setCaretPosition(0);
    }

    private void analyzeSingleDay() {
        String monthStr = (String) startMonthCombo.getSelectedItem();
        int day = (Integer) startDayCombo.getSelectedItem();
        Integer year = (Integer) startYearCombo.getSelectedItem();

        if (monthStr == null || year == null) {
            dateResultsTextArea.setText("Error: Please select a valid date.");
            return;
        }

        String formatted = String.format("%s %02d", monthStr, day);
        ArrayList<String> ips = analyzer.uniqueIPVisitsOnDay(formatted);

        StringBuilder sb = new StringBuilder();
        sb.append("Unique IP visits on ").append(formatted).append(":\n");
        sb.append("--------------------------------------\n");
        sb.append("Count: ").append(ips.size()).append("\n\n");
        if (ips.isEmpty()) {
            sb.append("(No visits recorded)");
        } else {
            for (String ip : ips) {
                sb.append(ip).append("\n");
            }
        }

        dateResultsTextArea.setText(sb.toString());
        dateResultsTextArea.setCaretPosition(0);
    }

    private void analyzeStatusCode() {
        int threshold = (int) numSpinner.getValue();
        String result = analyzer.getAllHigherThanNum(threshold);
        otherResultsTextArea.setText(result);
        otherResultsTextArea.setCaretPosition(0);
    }

    private Date createDate(int year, int month, int day, boolean startOfDay) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setLenient(false);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            if (startOfDay) {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            } else {
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
            }
            return cal.getTime();
        } catch (Exception e) {
            System.err.println("Invalid date: " + e.getMessage());
            return null;
        }
    }
}

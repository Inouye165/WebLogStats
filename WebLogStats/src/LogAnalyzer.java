// FILE: LogAnalyzer.java

import java.util.*; // Includes ArrayList, HashSet, Date, List
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Date; // Explicit import for clarity
import java.text.SimpleDateFormat; // Needed for the corrected method
import java.util.Locale;         // Needed by SimpleDateFormat

public class LogAnalyzer {
    private ArrayList<LogEntry> records;
    private Date minDate = null;
    private Date maxDate = null;

    public LogAnalyzer() {
        records = new ArrayList<LogEntry>();
    }

    public void readFile(String filename) throws IOException {
        records.clear();
        minDate = null;
        maxDate = null;
        List<String> lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
        for (String line : lines) {
            try {
                 if (line == null || line.trim().isEmpty()) continue;
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

    public Date getMinDate() { return minDate; }
    public Date getMaxDate() { return maxDate; }

    public int countUniqueIPs() { HashSet<String> u=new HashSet<>(); for(LogEntry le:records) if(le.getIpAddress()!=null) u.add(le.getIpAddress()); return u.size(); }
    public String getAllHigherThanNum(int n) { StringBuilder s=new StringBuilder(); s.append("--- Log entries with status code > ").append(n).append(" ---\n"); int c=0; for(LogEntry le:records) if(le.getStatusCode()>n){ s.append(le).append("\n"); c++; } if(c==0)s.append("None found.\n"); s.append("--- End Status Code > ").append(n).append(" ---"); return s.toString(); }
    public ArrayList<String> uniqueIPsInRange(int l, int h) { HashSet<String> u=new HashSet<>(); for(LogEntry le:records) {int s=le.getStatusCode(); if(s>=l&&s<=h&&le.getIpAddress()!=null) u.add(le.getIpAddress());} return new ArrayList<>(u); }
    public int countUniqueIPsInRange(int l, int h) { return uniqueIPsInRange(l, h).size(); }
    public ArrayList<String> uniqueIPVisitsOnDay(String someday) { HashSet<String> u=new HashSet<>(); SimpleDateFormat f=new SimpleDateFormat("MMM dd",Locale.US); for(LogEntry le:records){ Date d=le.getAccessTime(); if(d==null)continue; try{String fd=f.format(d); if(fd.equals(someday)&&le.getIpAddress()!=null) u.add(le.getIpAddress());}catch(Exception e){System.err.println("Err fmt date:"+d+e.getMessage());}} return new ArrayList<>(u); }
    public void printAll() { System.out.println("\n--- All Log Entries ---"); if(records.isEmpty())System.out.println("(No records loaded)"); else for(LogEntry le:records)System.out.println(le); System.out.println("--- End All Log Entries ---"); }

}
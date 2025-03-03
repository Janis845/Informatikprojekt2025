package org.rapla.plugin.availability.menu.server;

public class Availabilities {
    private String date;
    private String starttime;
    private String endtime;

    // Standard-Konstruktor (notwendig für JSON-Datenverarbeitung)
    public Availabilities() {}

    public Availabilities(String date, String starttime, String endtime) {
        this.date = date;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    // Getter und Setter
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStarttime() { return starttime; }
    public void setStarttime(String starttime) { this.starttime = starttime; }

    public String getEndtime() { return endtime; }
    public void setEndtime(String endtime) { this.endtime = endtime; }
}


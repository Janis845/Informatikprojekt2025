package org.rapla.plugin.availability.menu.server;

public class Availability {
    private String date;
    private String starttime;
    private String endtime;
    private String recurrence;
    private int weeks;

public Availability() {}    
    
    // Getter und Setter
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStarttime() { return starttime; }
    public void setStarttime(String starttime) { this.starttime = starttime; }

    public String getEndtime() { return endtime; }
    public void setEndtime(String endtime) { this.endtime = endtime; }

    @Override
    public String toString() {
        return "Availability{date='" + date + "', starttime='" + starttime + "', endtime='" + endtime + "'}";
    }

	public String getRecurrence() {
		return recurrence;
	}

	public void setRecurrence(String recurrence) {
		this.recurrence = recurrence;
	}

	public int getWeeks() {
		return weeks;
	}

	public void setWeeks(int weeks) {
		this.weeks = weeks;
	}
}

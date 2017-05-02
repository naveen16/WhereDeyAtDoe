package naveen16.wheredeyatdoe;

/**
 * Created by naveen on 4/28/17.
 */

public class Event {

    String date;
    String description;
    String location;
    String name;
    String startTime;
    String endTime;
    String entryFee;
    double latitude;
    double longitude;

    public Event(){

    }

    public Event(int i){
        name = "";
        date = "";
        startTime = "";
        endTime = "";
        location = "";
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Event(String d, String de, String loc, String n, String st, String et, String f, double lat, double lng){
        date = d;
        description = de;
        location = loc;
        name = n;
        startTime = st;
        endTime = et;
        entryFee = f;
        latitude = lat;
        longitude = lng;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(String entryFee) {
        this.entryFee = entryFee;
    }

    @Override
    public String toString() {
        return name+"\n"+date+"\n"+startTime+"-"+endTime+"\n"+location;
    }
}


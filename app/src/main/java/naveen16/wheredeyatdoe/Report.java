package naveen16.wheredeyatdoe;

import java.util.Date;

/**
 * Created by raghavrajvanshy on 3/22/17.
 */

public class Report {
    private String level;
    private Date timeOfEntry;

    public Date getTimeOfEntry() {
        return timeOfEntry;
    }

    public void setTimeOfEntry(Date time) {
        timeOfEntry = time;
    }

    public Report(String level, Date time){
        this.level=level;
        this.timeOfEntry=time;

    }
    public Report(){

    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String toString(){
        return "level:"+level+" TIME: "+timeOfEntry.toString();
    }
}

package naveen16.wheredeyatdoe;

/**
 * Created by raghavrajvanshy on 3/22/17.
 */

public class Report {
    private String level;
    private int numEntries;

    public int getNumEntries() {
        return numEntries;
    }

    public void setNumEntries(int numEntries) {
        this.numEntries = numEntries;
    }

    public Report(String level, int numEntries){
        this.level=level;
        this.numEntries=numEntries;

    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}

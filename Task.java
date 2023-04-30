/** Class that represents a general task */
public class Task {
    
    protected String name;
    protected String type;
    protected double startTime;
    protected double duration;
    protected int date;

    /** Constructor with given arguments */
    public Task(String name, String type, double startTime, double duration, int date) {
        this.name = name;
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
    }

    /** Displays task information. */
    public void print() {
        // left intentionally blank
    }

    /** Setter method for name.
        @param  name The name to be set. */
    public void setName(String name) {
        this.name = name;
    }

    /** Setter method for type.
        @param  type The type to be set. */
    public void setType(String type) {
        this.type = type;
    }

    /** Setter method for date.
        @param  date The date to be set. */
    public void setDate(int date) {
        this.date = date;
    }

    /** Setter method for start time.
        @param  startTime The start time to be set. */
    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    /** Setter method for duration.
        @param  duration The duration to be set. */
    public void setDuration(double duration) {
        this.duration = duration;
    }

    /** Getter method for name.
        @return  Name. */
    public String getName() {
        return name;
    }

    /** Getter method for type.
        @return  Type. */
    public String getType() {
        return type;
    }

    /** Getter method for date.
        @return  Date. */
    public int getDate() {
        return date;
    }

    /** Getter method for start time.
        @return  Start time. */
    public double getStartTime() {
        return startTime;
    }

    /** Getter method for duration.
        @return  Duration. */
    public double getDuration() {
        return duration;
    }
}

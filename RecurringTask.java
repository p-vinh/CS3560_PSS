/** Class that represents a recurring task */
public class RecurringTask extends Task {

    private int endDate;
    private int frequency;

    /** Constructor with given arguments */
    RecurringTask(String name, String type, double startTime, double duration, int date, int endDate, int frequency) {
        super(name, type, startTime, duration, date);
        this.endDate = endDate;
        this.frequency = frequency;
    }

    /** Displays task information. */
    @Override
    public void print() {
        System.out.println("Name: " + super.getName());
        System.out.println("Type: " + super.getType());
        System.out.println("Start Date: " + super.getDate());
        System.out.printf("Start Time: %.2f\n", super.getStartTime());
        System.out.printf("Duration: %.2f\n", super.getDuration());
        System.out.println("End Date: " + endDate);
        System.out.println("Frequency: " + frequency);
    }

    /** Setter method for end date.
        @param  endDate The end date to be set. */
    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    /** Setter method for frequency.
        @param  frequency The frequency to be set. */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /** Getter method for end date.
        @return  End date. */
    public int getEndDate() {
        return endDate;
    }

    /** Getter method for frequency.
        @return  Frequency. */
    public int getFrequency() {
        return frequency;
    }
}

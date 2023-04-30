/** Class that represents a transient task */
public class TransientTask extends Task {
    
    /** Constructor with given arguments */
    TransientTask(String name, String type, double time, double duration, int date) {
        super(name, type, time, duration, date);
    }

    /** Displays task information. */
    @Override
    public void print() {
        System.out.println("Name: " + super.getName());
        System.out.println("Type: " + super.getType());
        System.out.println("Date: " + super.getDate());
        System.out.printf("Start Time: %.2f\n", super.getStartTime());
        System.out.printf("Duration: %.2f\n", super.getDuration());
    }

}

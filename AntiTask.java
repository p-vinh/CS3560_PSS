/** Class that represents an antitask */
public class AntiTask extends Task {
    
    /** Constructor with given arguments */
    AntiTask(String name, String type, double startTime, double duration, int date) {
        super(name, type, startTime, duration, date);
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

/** Class that represents an antitask */
public class AntiTask extends Task {
    
    /** Constructor with given arguments */
    AntiTask(String name, String type, double startTime, double duration, int date) {
        super(name, type, startTime, duration, date);
    }

    // anti-task is used to cancel out one repetition of a recurring task 
    // recurring task and anti task must have same start time and duration 
    @Override
    public void print() {
        System.out.println("Name: " + super.getName());
        System.out.println("Type: " + super.getType());
        System.out.println("Date: " + super.getDate());
        System.out.printf("Start Time: %.2f\n", super.getStartTime());
        System.out.printf("Duration: %.2f\n", super.getDuration());
    }
}

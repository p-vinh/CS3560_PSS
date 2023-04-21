public class RecursiveTask extends Task {

    private int endDate;
    private int frequency;

    RecursiveTask(String name, String type, double time, double duration, int date, int endDate, int frequency) {
        super(name, type, time, duration, date);
        this.endDate = endDate;
        this.frequency = frequency;
    }

    @Override
    public void print() {
        System.out.println("Name: " + name);
        System.out.println("Type: " + type);
        System.out.printf("Start Time: %.2f\n", time);
        System.out.printf("Duration: %.2f\n", duration);
        System.out.println("Date: " + date);
        System.out.println("End Date: " + endDate);
        System.out.println();
    }
    
}

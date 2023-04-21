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
        System.out.println("Name: " + super.getName());
        System.out.println("Type: " + super.getType());
        System.out.printf("Start Time: %.2f\n", super.getTime());
        System.out.printf("Duration: %.2f\n", super.getDuration());
        System.out.println("Date: " + super.getDate());
        System.out.println("End Date: " + endDate);
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getEndDate() {
        return endDate;
    }

    public int getFrequency() {
        return frequency;
    }
    
}

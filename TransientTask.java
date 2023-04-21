public class TransientTask extends Task {
    
    TransientTask(String name, String type, double time, double duration, int date) {
        super(name, type, time, duration, date);
    }

    @Override
    public void print() {
        System.out.println("Name: " + super.getName());
        System.out.println("Type: " + super.getType());
        System.out.printf("Start Time: %.2f\n", super.getTime());
        System.out.printf("Duration: %.2f\n", super.getDuration());
        System.out.println("Date: " + super.getDate());
    }

}

public class TransientTask extends Task {
    
    TransientTask(String name, String type, double time, double duration, int date) {
        this.name = name;
        this.type = type;
        this.time = time;
        this.duration = duration;
        this.date = date;
    }

    @Override
    public void print() {
        System.out.println("Name: " + name);
        System.out.println("Type: " + type);
        System.out.printf("Start Time: %.2f\n", time);
        System.out.printf("Duration: %.2f\n", duration);
        System.out.println("Date: " + date);
        System.out.println();
    }

}
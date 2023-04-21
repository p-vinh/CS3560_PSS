public class Task {
    
    private String name;
    private String type;
    private double time;
    private double duration;
    private int date;
    
    private Task(String name, String type, double time, double duration, int date) {
        this.name = name;
        this.type = type;
        this.time = time;
        this.duration = duration;
        this.date = date;
    }

    public void print() {
        System.out.println("Name: " + name);
        System.out.println("Type: " + type);
        System.out.println("Time: " + time);
        System.out.println("Duration: " + duration);
        System.out.println("Date: " + date);
    }
}

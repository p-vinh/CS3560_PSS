public class Task {
    
    protected String name;
    protected String type;
    protected double time;
    protected double duration;
    protected int date;
    
    public Task(String name, String type, double time, double duration, int date) {
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

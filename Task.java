public class Task implements Comparable<Task>{
    
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
        // left intentionally blank
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getTime() {
        return time;
    }

    public double getDuration() {
        return duration;
    }

    public int getDate() {
        return date;
    }

    
    public int compareTo(Task task){
        int result = Integer.valueOf(date).compareTo(task.date);
        if (result == 0){
            return Double.valueOf(time).compareTo(task.time);
        }
        else{
            return result;
        }
    }
    

    
}

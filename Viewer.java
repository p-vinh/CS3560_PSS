import java.util.List;

public class Viewer {
    
    void displaySchedule(List<Task> list) {
        for (Task task : list) {
            task.print();
        }
    }
}

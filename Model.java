import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {

    List<Task> tasks = new ArrayList<Task>(); // Stores tasks
    Map<String, Boolean> names = new HashMap<>(); // Stores unique task names

    void createTask(Task task) {
        tasks.add(task);
        names.put(task.name, true);
    }

    List<Task> getTasks() {
        return tasks;
    }

    Map<String, Boolean> getNames() {
        return names;
    }
}
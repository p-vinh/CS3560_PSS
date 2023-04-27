import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Model {

    List<Task> tasks = new ArrayList<Task>(); // Stores tasks
    Map<String, Boolean> names = new HashMap<>(); // Stores unique task names

    void createTask(Task task) {
        tasks.add(task);
        names.put(task.getName(), true);
    }

    List<Task> getTasks() {
        return tasks;
    }

    Map<String, Boolean> getNames() {
        return names;
    }

    public boolean writeScheduleToFile() throws IOException {
        
        FileWriter write = new FileWriter(new File("schedule.txt"));
        StringBuilder sb = new StringBuilder();
        for (Task task : tasks) {

        }
        return false;
    }

    public boolean readScheduleToFile() throws IOException {

        return false;
    }
    
}
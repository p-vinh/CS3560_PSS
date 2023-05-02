import java.util.*;
import java.io.*;

public class Model {
    Scanner scnr = new Scanner(System.in);
    List<Task> listOfTasks = new ArrayList<Task>(); // Stores tasks
    Map<String, Boolean> taskName = new HashMap<>(); // Stores unique task names
    
    int numInput;

    void displayTaskOptions(){
        System.out.println("Choose a task");
        System.out.println("1. Transient Task");
        System.out.println("2. Recurring Task");
        System.out.println("3. Anti-task");
    }

    
    void createTask(Task task) {
        listOfTasks.add(task);
        taskName.put(task.getName(), true);
    }

    List<Task> getTasks() {
        return listOfTasks;
    }

    Map<String, Boolean> getNames() {
        return taskName;
    }
    
}
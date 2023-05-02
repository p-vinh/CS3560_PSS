<<<<<<< HEAD
import java.util.*;
import java.io.*;
=======
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

>>>>>>> 383a533478c344f49e83475301b607d0fa2adc48

public class Model {
    Scanner scnr = new Scanner(System.in);
    List<Task> listOfTasks = new ArrayList<Task>(); // Stores tasks
    Map<String, Boolean> taskName = new HashMap<>(); // Stores unique task names
    
    int numInput;

<<<<<<< HEAD
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
    
=======
    private List<Task> tasks = new ArrayList<Task>(); // Stores tasks
    private Set<String> names = new HashSet<>(); // Stores unique task names
    private Schedule schedular = new Schedule();

    public void createTask(Task task) {
        tasks.add(task);
        names.add(task.getName());
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Set<String> getNames() {
        return names;
    }

    public List<Task> getDailySchedule(int date) {
        return schedular.dailySchedule(tasks, date);
    }

    public List<Task> getWeeklySchedule(int date) {
        return schedular.weeklySchedule(tasks, date);
    }

    public List<Task> getMonthlySchedule(int date) {
        return schedular.monthlySchedule(tasks, date);
    }

    public List<Task> getFullSchedule() {
        return schedular.fullSchedule(tasks);
    }

    public void writeScheduleToFile(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder("[\n\t");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            sb.append("{\n\t\t"
            + "\"Name\": \"" + task.getName() + "\",\n\t\t"
            + "\"Type\": \"" + task.getType() + "\",\n\t\t"
            + "\"Date\": " + task.getDate() + ",\n\t\t"
            + "\"StartTime\": " + task.getTime() + ",\n\t\t"
            + "\"Duration\": " + task.getDuration()
            + "\n\t}");
            if (i < tasks.size() - 1) {
                sb.append(",\n\t");
            }
        }
        sb.append("\n]");

        try {
            FileWriter writer = new FileWriter(new File(fileName + ".json"));
            writer.write(sb.toString());
            writer.close();
            System.out.println("Successfully wrote schedule to " + fileName + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readScheduleToFile(String fileName) throws IOException {

    }
>>>>>>> 383a533478c344f49e83475301b607d0fa2adc48
}
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model {

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

    public void writeScheduleToFile(List<Task> schedule, String fileName) {
        String json = "[\n\t";
        for (int i = 0; i < schedule.size(); i++) {
            Task task = schedule.get(i);
            json += "{\n\t\t";
            json += "\"Name\": \"" + task.getName() + "\",\n\t\t";
            json += "\"Type\": \"" + task.getType() + "\",\n\t\t";
            json += "\"Date\": \"" + task.getDate() + "\",\n\t\t";
            json += "\"StartTime\": " + task.getTime() + ",\n\t\t";
            json += "\"Duration\": " + task.getDuration();
            json += "\n\t}";
            if (i < tasks.size() - 1) {
                json += ",\n\t";
            }
        }
        json += "\n]";

        try {
            FileWriter writer = new FileWriter(fileName + ".json");
            writer.write(json);
            writer.close();
            System.out.println("Successfully wrote schedule to " + fileName + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
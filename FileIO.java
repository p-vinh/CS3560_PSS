import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Class for managing read and write to file operations */
public class FileIO {

    private Model model;
    private Calendar calendar;

    public FileIO(Model model, Calendar calendar) {
        this.model = model;
        this.calendar = calendar;
    }

    /**
     * Writes a schedule to a file.
     * 
     * @param schedule The schedule to be written.
     * @param fileName The name of the file to write to.
     */
    public void writeScheduleToFile(List<Task> schedule, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder("[\n\t");
        for (int i = 0; i < schedule.size(); i++) {
            Task task = schedule.get(i);
            sb.append("{\n\t\t"
                    + "\"Name\": \"" + task.getName() + "\",\n\t\t"
                    + "\"Type\": \"" + task.getType() + "\",\n\t\t");
            if (task.getClass() == TransientTask.class || task.getClass() == AntiTask.class) {
                sb.append("\"Date\": " + task.getDate() + ",\n\t\t");
            } else {
                sb.append("\"StartDate\": " + task.getDate() + ",\n\t\t");
            }
            sb.append("\"StartTime\": " + task.getStartTime() + ",\n\t\t"
                    + "\"Duration\": " + task.getDuration());
            if (task.getClass() == RecurringTask.class) {
                RecurringTask recurringTask = (RecurringTask) task;
                sb.append(",\n\t\t\"EndDate\": " + recurringTask.getEndDate() + ",\n\t\t"
                        + "\"Frequency\": " + recurringTask.getFrequency());
            }
            sb.append("\n\t}");
            if (i < schedule.size() - 1) {
                sb.append(",\n\t");
            }
        }
        sb.append("\n]");

        try {
            FileWriter writer = new FileWriter(new File(fileName + ".json"));
            writer.write(sb.toString());
            writer.close();
            System.out.println("------------------------------");
            System.out.println("Successfully wrote schedule to " + fileName + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a schedule from a given file.
     * 
     * @param fileName The name of the file holding a valid schedule in JSON format.
     */
    public void readScheduleFromFile(String fileName) throws IOException {
        List<Task> backup = new ArrayList<Task>(); // Stores a copy in case reading fails
        for (Task task : model.getTasks()) {
            backup.add(task);
        }

        boolean created = false;
        model.setIsReading(true);

        Scanner scanner = new Scanner(new File(fileName));

        // Read the entire file as one string
        scanner.useDelimiter("\\Z");
        String fileContent = scanner.next();

        try {
            // Use a regular expression to match JSON objects, identify JSON objects in { }
            Pattern pattern = Pattern.compile("\\{.*?\\}", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(fileContent);

            // Iterate through each JSON object
            while (matcher.find()) {
                String json = matcher.group();

                // Extract the values of each key from the JSON object
                String name = extractString(json, "Name");
                String type = extractString(json, "Type");
                Double startTime = extractDouble(json, "StartTime");
                Double duration = extractDouble(json, "Duration");
                if (!(startTime >= 0 && startTime <= 23.75 && startTime % 0.25 == 0)) {
                    throw new IllegalArgumentException(
                            "Error: File contains task \"" + name + "\" with an invalid time: " + startTime.toString());
                }
                if (!(duration >= 0.25 && duration <= 23.75 && duration % 0.25 == 0)) {
                    throw new IllegalArgumentException("Error: File contains task \"" + name
                            + "\" with an invalid duration: " + duration.toString());
                }

                type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();

                // Check which type of task the JSON object represents by type
                if (type.equals("Visit") || type.equals("Shopping") || type.equals("Appointment")) {
                    Integer date = extractInt(json, "Date");
                    if (!calendar.isValidDate(date)) {
                        throw new IllegalArgumentException(
                                "Error: File contains task \"" + name + "\" with an invalid date: " + date.toString());
                    }
                    created = model.createTask(new TransientTask(name, type, startTime, duration, date));
                    if (!created) {
                        break;
                    }
                } else if (type.equals("Class") || type.equals("Study") || type.equals("Sleep")
                        || type.equals("Exercise") || type.equals("Work") || type.equals("Meal")) {
                    Integer startDate = extractInt(json, "StartDate");
                    Integer endDate = extractInt(json, "EndDate");
                    Integer frequency = extractInt(json, "Frequency");
                    if (!calendar.isValidDate(startDate)) {
                        throw new IllegalArgumentException("Error: File contains task \"" + name
                                + "\" with an invalid start date: " + startDate.toString());
                    }
                    if (!calendar.isValidDate(endDate)) {
                        throw new IllegalArgumentException("Error: File contains task \"" + name
                                + "\" with an invalid end date: " + endDate.toString());
                    }
                    if (startDate > endDate) {
                        throw new IllegalArgumentException(
                                "Error: File contains task \"" + name + "\" with an invalid date period: "
                                        + startDate.toString() + " - " + endDate.toString());
                    }
                    if (frequency < 0 || frequency > 7) {
                        throw new IllegalArgumentException("Error: File contains task \"" + name
                                + "\" with an invalid frequency: " + frequency.toString());
                    }
                    created = model.createTask(
                            new RecurringTask(name, type, startTime, duration, startDate, endDate, frequency));
                    if (!created) {
                        break;
                    }
                } else if (type.equals("Cancellation")) {
                    Integer date = extractInt(json, "Date");
                    if (!calendar.isValidDate(date)) {
                        throw new IllegalArgumentException(
                                "Error: File contains task \"" + name + "\" with an invalid date: " + date.toString());
                    }
                    created = model.createTask(new AntiTask(name, type, startTime, duration, date));
                    if (!created) {
                        break;
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Error: File contains task \"" + name + "\" with an invalid type: " + type);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("------------------------------");
            System.out.println(e.getMessage());
            created = false;
        } catch (IllegalStateException e) {
            System.out.println("------------------------------");
            System.out.println("Error: File is either not in JSON format or contains inconsistent values");
            created = false;
        }
        if (!created) {
            List<Task> tempTasks = new ArrayList<Task>(); // If read fails, restore tasks and names
            Set<String> tempNames = new HashSet<String>();
            for (Task task : backup) {
                tempTasks.add(task);
                tempNames.add(task.getName());
            }

            model.setTasks(tempTasks); // Changing reference variable
            model.setNames(tempNames);

        } else {
            System.out.println("------------------------------");
            System.out.println("Successfully read schedule");
        }
        model.setIsReading(false);
    }

    /**
     * Extracts the string value of a given JSON object by key.
     * 
     * @param json The JSON object as a string.
     * @param key  The key of the JSON object.
     * @return The string value of a given key.
     */
    private String extractString(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(json);
        matcher.find();
        return matcher.group(1);
    }

    /**
     * Extracts the floating-point value of a given JSON object by key.
     * 
     * @param json The JSON object as a string.
     * @param key  The key of the JSON object.
     * @return The floating-point value of a given key.
     */
    private Double extractDouble(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(json);
        matcher.find();
        return Double.parseDouble(matcher.group(1));
    }

    /**
     * Extracts the integer value of a given JSON object by key.
     * 
     * @param json The JSON object as a string.
     * @param key  The key of the JSON object.
     * @return The integer value of a given key.
     */
    private Integer extractInt(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        matcher.find();
        return Integer.parseInt(matcher.group(1));
    }
}

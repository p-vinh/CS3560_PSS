import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Class for managing all tasks */
public class Model {

    private List<Task> tasks = new ArrayList<Task>(); // Stores tasks
    private Set<String> names = new HashSet<String>(); // Stores unique task names
    private Scheduler scheduler = new Scheduler(); // Scheduler for creating a schedule
    private Calendar calendar = new Calendar(); // Calendar for checking dates and overlaps

    private boolean isUpdating = false; // Flag for a task being edited
    private boolean isReading = false; // Flag for a file holding a schedule being read

    /** Creates a given task.
        @param  task The task to be created. 
        @return  True if the task was successfully created, false otherwise. */
    public boolean createTask(Task task) {
        if (isReading) {
            if (names.contains(task.getName())) {
                System.out.println("------------------------------");
                System.out.println("Error: Failed to read file due to an existing task name: \"" + task.getName() + "\"");
                return false;
            }
        }
        if (task.getClass() == TransientTask.class) {
            if (tasks.isEmpty()) {
                tasks.add(task);
                names.add(task.getName());
                if (!isReading) {
                    System.out.println("------------------------------");
                    if (isUpdating) {
                        System.out.println("Successfully updated task");
                    } else {
                        System.out.println("Successfully created task");
                    }
                }
                return true;
            }
            for (int i = 0; i < tasks.size(); i++) {
                Task potentialOverlappingTask = tasks.get(i);
                if (calendar.checkOverlap(task, potentialOverlappingTask) && potentialOverlappingTask.getClass() == TransientTask.class) {
                    System.out.println("------------------------------");
                    if (isReading) {
                        System.out.println("Error: Failed to read file due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                    } else if (isUpdating) {
                        System.out.println("Error: Failed to update task due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                    } else {
                        System.out.println("Error: Failed to create task due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                    }
                    return false;
                } else if (calendar.checkOverlap(task, potentialOverlappingTask) && potentialOverlappingTask.getClass() == RecurringTask.class) {
                    int date = potentialOverlappingTask.getDate();
                    double time = potentialOverlappingTask.getStartTime();
                    double duration = potentialOverlappingTask.getDuration();
                    boolean antiTaskFound = false;
                    for (int j = i + 1; j < tasks.size(); j++) {
                        if (tasks.get(j).getClass() == AntiTask.class) {
                            Task antiTask = tasks.get(j);
                            if (antiTask.getDate() == date && antiTask.getStartTime() == time && antiTask.getDuration() == duration) {
                                antiTaskFound = true;
                                break;
                            }
                        }
                    }
                    if (!antiTaskFound) {
                        System.out.println("------------------------------");
                        if (isReading) {
                            System.out.println("Error: Failed to read file due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                        } else if (isUpdating) {
                            System.out.println("Error: Failed to update task due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                        } else {
                            System.out.println("Error: Failed to create task due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                        }
                        return false;
                    }
                }
            }
            tasks.add(task);
            names.add(task.getName());
            if (!isReading) {
                System.out.println("------------------------------");
                if (isUpdating) {
                    System.out.println("Successfully updated task");
                } else {
                    System.out.println("Successfully created task");
                }
            }
            return true;
        } else if (task.getClass() == RecurringTask.class) {
            RecurringTask recurringTask = (RecurringTask) task;
            if (tasks.isEmpty()) {
                for (int date = recurringTask.getDate(); date <= recurringTask.getEndDate();) {
                    tasks.add(new RecurringTask(recurringTask.getName(), recurringTask.getType(), recurringTask.getStartTime(), recurringTask.getDuration(), date, recurringTask.getEndDate(), recurringTask.getFrequency()));
                    LocalDate currentDate = LocalDate.parse(String.valueOf(date), DateTimeFormatter.BASIC_ISO_DATE);
                    LocalDate nextDate = currentDate.plusDays(recurringTask.getFrequency());
                    date = Integer.parseInt(nextDate.format(DateTimeFormatter.BASIC_ISO_DATE));
                }
                names.add(recurringTask.getName());
                if (!isReading) {
                    System.out.println("------------------------------");
                    if (isUpdating) {
                        System.out.println("Successfully updated task(s)");
                    } else {
                        System.out.println("Successfully created task(s)");
                    }
                }
                return true;
            }
            for (int date = recurringTask.getDate(); date <= recurringTask.getEndDate();) {
                for (int i = 0; i < tasks.size(); i++) {
                    Task potentialOverlappingTask = tasks.get(i);
                    if (calendar.checkOverlap(new RecurringTask(recurringTask.getName(), recurringTask.getType(), recurringTask.getStartTime(), recurringTask.getDuration(), date, recurringTask.getEndDate(), recurringTask.getFrequency()), potentialOverlappingTask) && potentialOverlappingTask.getClass() == TransientTask.class) {
                        System.out.println("------------------------------");
                        if (isReading) {
                            System.out.println("Error: Failed to read file due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                        } else if (isUpdating) {
                            System.out.println("Error: Failed to update task due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                        } else {
                            System.out.println("Error: Failed to create task due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                        }
                        return false;
                    } else if (calendar.checkOverlap(new RecurringTask(recurringTask.getName(), recurringTask.getType(), recurringTask.getStartTime(), recurringTask.getDuration(), date, recurringTask.getEndDate(), recurringTask.getFrequency()), potentialOverlappingTask) && potentialOverlappingTask.getClass() == RecurringTask.class) {
                        int day = potentialOverlappingTask.getDate();
                        double time = potentialOverlappingTask.getStartTime();
                        double duration = potentialOverlappingTask.getDuration();
                        boolean antiTaskFound = false;
                        for (int j = i + 1; j < tasks.size(); j++) {
                            if (tasks.get(j).getClass() == AntiTask.class) {
                                Task antiTask = tasks.get(j);
                                if (antiTask.getDate() == day && antiTask.getStartTime() == time && antiTask.getDuration() == duration) {
                                    antiTaskFound = true;
                                    break;
                                }
                            }
                        }
                        if (!antiTaskFound) {
                            System.out.println("------------------------------");
                            if (isReading) {
                                System.out.println("Error: Failed to read file due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                            } else if (isUpdating) {
                                System.out.println("Error: Failed to update task due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                            } else {
                                System.out.println("Error: Failed to create task due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                            }
                            return false;
                        }
                    }
                }
                LocalDate currentDate = LocalDate.parse(String.valueOf(date), DateTimeFormatter.BASIC_ISO_DATE);
                LocalDate nextDate = currentDate.plusDays(recurringTask.getFrequency());
                date = Integer.parseInt(nextDate.format(DateTimeFormatter.BASIC_ISO_DATE));
            }
            for (int date = recurringTask.getDate(); date <= recurringTask.getEndDate();) {
                tasks.add(new RecurringTask(recurringTask.getName(), recurringTask.getType(), recurringTask.getStartTime(), recurringTask.getDuration(), date, recurringTask.getEndDate(), recurringTask.getFrequency()));
                LocalDate currentDate = LocalDate.parse(String.valueOf(date), DateTimeFormatter.BASIC_ISO_DATE);
                LocalDate nextDate = currentDate.plusDays(recurringTask.getFrequency());
                date = Integer.parseInt(nextDate.format(DateTimeFormatter.BASIC_ISO_DATE));
            }
            names.add(recurringTask.getName());
            if (!isReading) {
                System.out.println("------------------------------");
                if (isUpdating) {
                    System.out.println("Successfully updated task");
                } else {
                    System.out.println("Successfully created task");
                }
            }
            return true;
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getClass() == RecurringTask.class) {
                    Task recurringTask = tasks.get(i);
                    if (recurringTask.getDate() == task.getDate() && recurringTask.getStartTime() == task.getStartTime() && recurringTask.getDuration() == task.getDuration()) {
                        boolean antiTaskFound = false;
                        int date = recurringTask.getDate();
                        double time = recurringTask.getStartTime();
                        double duration = recurringTask.getDuration();
                        for (int j = i + 1; j < tasks.size(); j++) {
                            if (tasks.get(j).getClass() == AntiTask.class) {
                                Task antiTask = tasks.get(j);
                                if (antiTask.getDate() == date && antiTask.getStartTime() == time && antiTask.getDuration() == duration) {
                                    antiTaskFound = true;
                                    break;
                                }
                            }
                        }
                        if (antiTaskFound) {
                            continue;
                        }
                        tasks.add(task);
                        names.add(task.getName());
                        if (!isReading) {
                            System.out.println("------------------------------");
                            if (isUpdating) {
                                System.out.println("Successfully updated task");
                            } else {
                                System.out.println("Successfully created task");
                            }
                        }
                        return true;
                    }
                }
            }
            System.out.println("------------------------------");
            if (isReading) {
                System.out.println("Error: Failed to read file when creating cancellation task due to no instance of a recurring task found");
            } else if (isUpdating) {
                System.out.println("Error: Failed to update task due to no instance of a recurring task found");
            } else {
                System.out.println("Error: Failed to create task due to no instance of a recurring task found");
            }
            return false;
        }
    }

    /** Fetches a task by name.
        @param  name The name of the task to be fetched. 
        @return  The task, null if no task is found. */
    public Task viewTask(String name) {
        if (!names.contains(name)) {
            return null;
        }
        for (Task task : tasks) {
            if (task.getName().equals(name)) {
                return task;
            }
        }
        return null;
    }


    /** Edits a task given by name.
        @param  oldTask The old task information to be updated.
        @param  newTask The new task information to update to. 
        @return  True if the task was successfully updated, false otherwise. */
    public boolean editTask(Task oldTask, Task newTask) {
        if (oldTask.getClass() == newTask.getClass()) {
            if (oldTask.getClass() == TransientTask.class || oldTask.getClass() == AntiTask.class) {
                if (oldTask.getDate() == newTask.getDate()
                    && oldTask.getStartTime() == newTask.getStartTime()
                    && oldTask.getDuration() == newTask.getDuration()) {
                    for (Task task : tasks) {
                        if (task.getName().equals(oldTask.getName())) {
                            task.setName(newTask.getName());
                            task.setType(newTask.getType());
                            names.remove(oldTask.getName());
                            names.add(newTask.getName());
                            System.out.println("------------------------------");
                            System.out.println("Successfully updated task");
                            return true;
                        }
                    }
                } else {
                    List<Task> backup = new ArrayList<Task>(); // Stores a copy in case update fails
                    for (Task task : tasks) {
                        backup.add(task);
                    }
                    isUpdating = true;
                    boolean deleted = deleteTask(oldTask.getName());
                    if (deleted) {
                        boolean created = createTask(newTask);
                        if (created) {
                            isUpdating = false;
                            return true;
                        } else {
                            tasks = new ArrayList<Task>(); // If update fails, restore tasks and names
                            names = new HashSet<String>();
                            for (Task task : backup) {
                                tasks.add(task);
                                names.add(task.getName());
                            }
                        }
                    }
                }
            } else {
                RecurringTask oldRecurringTask = (RecurringTask) oldTask;
                RecurringTask newRecurringTask = (RecurringTask) newTask;
                if (oldRecurringTask.getDate() == newRecurringTask.getDate()
                        && oldRecurringTask.getStartTime() == newRecurringTask.getStartTime()
                        && oldRecurringTask.getDuration() == newRecurringTask.getDuration()
                        && oldRecurringTask.getEndDate() == newRecurringTask.getEndDate()
                        && oldRecurringTask.getFrequency() == newRecurringTask.getFrequency()) {
                    for (Task task : tasks) {
                        if (task.getName().equals(oldTask.getName())) {
                            task.setName(newTask.getName());
                            task.setType(newTask.getType());
                        }
                    }
                    names.remove(oldTask.getName());
                    names.add(newTask.getName());
                    System.out.println("------------------------------");
                    System.out.println("Successfully updated task(s)");
                    return true;
                } else {
                    List<Task> backup = new ArrayList<Task>(); // Stores a copy in case update fails
                    for (Task task : tasks) {
                        backup.add(task);
                    }
                    List<Task> antiTasksList = new ArrayList<Task>(); // Stores any associated antitasks about to be deleted
                    List<Task> moveTasksList = new ArrayList<Task>(); // Moves any tasks created after the old recurring task to after the new recurring task to keep order of creation
                    for (int i = 0; i < tasks.size(); i++) {
                        Task task = tasks.get(i);
                        if (task.getName().equals(oldRecurringTask.getName())) {
                            for (int j = i; j < tasks.size(); j++) {
                                Task potentialRecurringTask = tasks.get(j);
                                if (potentialRecurringTask.getName().equals(oldRecurringTask.getName())) {
                                    int date = potentialRecurringTask.getDate();
                                    double startTime = potentialRecurringTask.getStartTime();
                                    double duration = potentialRecurringTask.getDuration();
                                    for (int k = j + 1; k < tasks.size(); k++) {
                                        Task potentialAntiTask = tasks.get(k);
                                        if (potentialAntiTask.getClass() == AntiTask.class
                                                && potentialAntiTask.getDate() == date
                                                && potentialAntiTask.getStartTime() == startTime
                                                && potentialAntiTask.getDuration() == duration) {
                                            tasks.remove(potentialAntiTask); // Remove any matching antitask
                                            names.remove(potentialAntiTask.getName());
                                            antiTasksList.add(potentialAntiTask); // Store the antitask in case an instance of the new recurring task matches
                                            break;
                                        }
                                    }
                                    tasks.remove(potentialRecurringTask); // Remove any recurring task instance
                                    j--; // Decrement j to account for removed task
                                } else {
                                    moveTasksList.add(tasks.get(j));
                                    tasks.remove(tasks.get(j));
                                    j--;
                                }
                            }
                            names.remove(oldRecurringTask.getName());
                        }
                    }
                    for (int date = newRecurringTask.getDate(); date <= newRecurringTask.getEndDate();) {
                        for (int i = 0; i < tasks.size(); i++) {
                            Task potentialOverlappingTask = tasks.get(i);
                            if (calendar.checkOverlap(new RecurringTask(newRecurringTask.getName(), newRecurringTask.getType(), newRecurringTask.getStartTime(), newRecurringTask.getDuration(), date, newRecurringTask.getEndDate(), newRecurringTask.getFrequency()), potentialOverlappingTask) && potentialOverlappingTask.getClass() == TransientTask.class) {
                                int day = newRecurringTask.getDate();
                                double time = newRecurringTask.getStartTime();
                                double duration = newRecurringTask.getDuration();
                                boolean antiTaskFound = false;
                                for (int j = 0; j < antiTasksList.size(); j++) {
                                    if (antiTasksList.get(j).getDate() == day && antiTasksList.get(j).getStartTime() == time && antiTasksList.get(j).getDuration() == duration) {
                                        antiTaskFound = true;
                                        break;
                                    }
                                }
                                if (!antiTaskFound) {
                                    System.out.println("------------------------------");
                                    System.out.println("Error: Failed to update task due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                                    tasks = new ArrayList<Task>(); // If update fails, restore tasks and names
                                    names = new HashSet<String>();
                                    for (Task task : backup) {
                                        tasks.add(task);
                                        names.add(task.getName());
                                    }
                                    isUpdating = false;
                                    return false;
                                }
                            } else if (calendar.checkOverlap(new RecurringTask(newRecurringTask.getName(), newRecurringTask.getType(), newRecurringTask.getStartTime(), newRecurringTask.getDuration(), date, newRecurringTask.getEndDate(), newRecurringTask.getFrequency()), potentialOverlappingTask) && potentialOverlappingTask.getClass() == RecurringTask.class) {
                                int day = newRecurringTask.getDate();
                                double time = newRecurringTask.getStartTime();
                                double duration = newRecurringTask.getDuration();
                                boolean antiTaskFound = false;
                                for (int j = 0; j < antiTasksList.size(); j++) {
                                    if (antiTasksList.get(j).getDate() == day && antiTasksList.get(j).getStartTime() == time && antiTasksList.get(j).getDuration() == duration) {
                                        antiTaskFound = true;
                                        break;
                                    }
                                }
                                if (antiTaskFound) {
                                   continue;
                                }
                                day = potentialOverlappingTask.getDate();
                                time = potentialOverlappingTask.getStartTime();
                                duration = potentialOverlappingTask.getDuration();
                                antiTaskFound = false;
                                for (int j = i + 1; j < tasks.size(); j++) {
                                    if (tasks.get(j).getClass() == AntiTask.class) {
                                        Task antiTask = tasks.get(j);
                                        if (antiTask.getDate() == day && antiTask.getStartTime() == time && antiTask.getDuration() == duration) {
                                            antiTaskFound = true;
                                            break;
                                        }
                                    }
                                }
                                if (!antiTaskFound) {
                                    System.out.println("------------------------------");
                                    System.out.println("Error: Failed to update task due to an overlapping conflict with task: \"" + potentialOverlappingTask.getName() + "\"");
                                    tasks = new ArrayList<Task>(); // If update fails, restore tasks and names
                                    names = new HashSet<String>();
                                    for (Task task : backup) {
                                        tasks.add(task);
                                        names.add(task.getName());
                                    }
                                    isUpdating = false;
                                    return false;
                                }
                            }
                        }
                        LocalDate currentDate = LocalDate.parse(String.valueOf(date), DateTimeFormatter.BASIC_ISO_DATE);
                        LocalDate nextDate = currentDate.plusDays(newRecurringTask.getFrequency());
                        date = Integer.parseInt(nextDate.format(DateTimeFormatter.BASIC_ISO_DATE));
                    }
                    for (int date = newRecurringTask.getDate(); date <= newRecurringTask.getEndDate();) {
                        tasks.add(new RecurringTask(newRecurringTask.getName(), newRecurringTask.getType(), newRecurringTask.getStartTime(), newRecurringTask.getDuration(), date, newRecurringTask.getEndDate(), newRecurringTask.getFrequency()));
                        LocalDate currentDate = LocalDate.parse(String.valueOf(date), DateTimeFormatter.BASIC_ISO_DATE);
                        LocalDate nextDate = currentDate.plusDays(newRecurringTask.getFrequency());
                        date = Integer.parseInt(nextDate.format(DateTimeFormatter.BASIC_ISO_DATE));
                    }
                    names.add(newRecurringTask.getName());
                    for (Task antiTask : antiTasksList) {
                        for (int i = 0; i < tasks.size(); i++) {
                            if (antiTask.getDate() == tasks.get(i).getDate() && antiTask.getStartTime() == tasks.get(i).getStartTime() && antiTask.getDuration() == tasks.get(i).getDuration()) {
                                tasks.add(antiTask); // Add antitask back if it matches an instance of the new recurring task
                                names.add(antiTask.getName());
                                break;
                            }
                        }
                    }
                    for (Task task : moveTasksList) {
                        tasks.add(task); // Add tasks back to the end of the list
                    }
                    System.out.println("------------------------------");
                    System.out.println("Successfully updated task(s)");
                    return true;
                }
            }
        } else {
            List<Task> backup = new ArrayList<Task>(); // Stores a copy in case update fails
            for (Task task : tasks) {
                backup.add(task);
            }
            isUpdating = true;
            boolean deleted = deleteTask(oldTask.getName());
            if (deleted) {
                boolean created = createTask(newTask);
                if (created) {
                    isUpdating = false;
                    return true;
                } else {
                    tasks = new ArrayList<Task>(); // If update fails, restore tasks and names
                    names = new HashSet<String>();
                    for (Task task : backup) {
                        tasks.add(task);
                        names.add(task.getName());
                    }
                }
            }
        }
        isUpdating = false;
        return false;
    }

    /** Deletes a task by name.
        @param  name The name of the task to be deleted. 
        @return  True if the task was successfully deleted, false otherwise. */
    public boolean deleteTask(String name) {
        if (!names.contains(name)) {
            System.out.println("------------------------------");
            System.out.println("No tasks found with given name");
            return false;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getName().equals(name)) {
                if (task.getClass() == TransientTask.class) {
                    tasks.remove(task); // Remove transient task
                    names.remove(name);
                    if (!isUpdating) {
                        System.out.println("------------------------------");
                        System.out.println("Successfully deleted task: " + "\"" + name + "\"");
                    }
                    return true;
                } else if (task.getClass() == RecurringTask.class) {
                    for (int j = i; j < tasks.size(); j++) {
                        Task potentialRecurringTask = tasks.get(j);
                        if (potentialRecurringTask.getName().equals(name)) {
                            int date = potentialRecurringTask.getDate();
                            double startTime = potentialRecurringTask.getStartTime();
                            double duration = potentialRecurringTask.getDuration();
                            for (int k = j + 1; k < tasks.size(); k++) {
                                Task potentialAntiTask = tasks.get(k);
                                if (potentialAntiTask.getClass() == AntiTask.class
                                        && potentialAntiTask.getDate() == date
                                        && potentialAntiTask.getStartTime() == startTime
                                        && potentialAntiTask.getDuration() == duration) {
                                    tasks.remove(potentialAntiTask); // Remove any matching antitask
                                    names.remove(potentialAntiTask.getName());
                                    break;
                                }
                            }
                            tasks.remove(potentialRecurringTask); // Remove any recurring task instance
                            j--; // Decrement j to account for removed task
                        }
                    }
                    names.remove(name);
                    if (!isUpdating) {
                        System.out.println("------------------------------");
                        System.out.println("Successfully deleted task(s): " + "\"" + name + "\", including any cancellation tasks associated");
                    }
                    return true;
                } else if (task.getClass() == AntiTask.class) {
                    for (int j = 0; j < tasks.size(); j++) {
                        if (tasks.get(j).getClass() == RecurringTask.class) {
                            Task recurringTask = tasks.get(j);
                            int date = recurringTask.getDate();
                            double startTime = recurringTask.getStartTime();
                            double duration = recurringTask.getDuration();
                            if (task.getDate() == date && task.getStartTime() == startTime && task.getDuration() == duration) { // found matching recurring
                                for (int k = j + 1; k < tasks.size(); k++) {
                                    if (tasks.get(k).getClass() == AntiTask.class
                                        && tasks.get(k).getDate() == date
                                        && tasks.get(k).getStartTime() == startTime
                                        && tasks.get(k).getDuration() == duration) {
                                        if (!tasks.get(k).getName().equals(name)) {
                                            break;
                                        } else {
                                            for (int l = k + 1; l < tasks.size(); l++) {
                                                if (calendar.checkOverlap(recurringTask, tasks.get(l)) && tasks.get(l).getClass() == TransientTask.class) {
                                                    System.out.println("------------------------------");
                                                    if (!isUpdating) {
                                                        System.out.println("Error: Failed to delete cancellation task, the tasks: \"" + recurringTask.getName() + "\" and \"" + tasks.get(l).getName() + "\" have overlapping conflicts");
                                                    } else {
                                                        System.out.println("Error: Failed to update task, the tasks: \"" + recurringTask.getName() + "\" and \"" + tasks.get(l).getName() + "\" have overlapping conflicts");
                                                    }
                                                    return false;
                                                } else if (calendar.checkOverlap(recurringTask, tasks.get(l)) && tasks.get(l).getClass() == RecurringTask.class) {
                                                    boolean antiTaskFound = false;
                                                    for (int m = l + 1; m < tasks.size(); m++) {
                                                        if (tasks.get(m).getClass() == AntiTask.class
                                                            && tasks.get(m).getDate() == tasks.get(l).getDate()
                                                            && tasks.get(m).getStartTime() == tasks.get(l).getStartTime()
                                                            && tasks.get(m).getDuration() == tasks.get(l).getDuration()) {
                                                            antiTaskFound = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!antiTaskFound) {
                                                        System.out.println("------------------------------");
                                                        if (!isUpdating) {
                                                            System.out.println("Error: Failed to delete cancellation task, the tasks: \"" + recurringTask.getName() + "\" and \"" + tasks.get(l).getName() + "\" have overlapping conflicts");
                                                        } else {
                                                            System.out.println("Error: Failed to update task, the tasks: \"" + recurringTask.getName() + "\" and \"" + tasks.get(l).getName() + "\" have overlapping conflicts");
                                                        }
                                                        return false;
                                                    }
                                                }
                                            }
                                            tasks.remove(task); // Remove antitask
                                            names.remove(name);
                                            if (!isUpdating) {
                                                System.out.println("------------------------------");
                                                System.out.println("Successfully deleted task: " + "\"" + name + "\"");
                                            }
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    } 

    /** Getter method for list of tasks.
        @return  The list of tasks. */
    public List<Task> getTasks() {
        return tasks;
    }

    /** Getter method for set of tasks names.
        @return  The set of task names. */
    public Set<String> getNames() {
        return names;
    }

    /** Requests a daily schedule of a given date.
        @param  date The given date
        @return  Daily schedule. */
    public List<Task> getDailySchedule(int date) {
        return scheduler.dailySchedule(tasks, date);
    }

    /** Requests a weekly schedule of a given date.
        @param  date First day of the weekly schedule
        @return  Weekly schedule. */
    public List<Task> getWeeklySchedule(int date) {
        return scheduler.weeklySchedule(tasks, date);
    }

    /** Requests a monthly schedule of a given date.
        @param  date First day of the monthly schedule
        @return  Monthly schedule. */
    public List<Task> getMonthlySchedule(int date) {
        return scheduler.monthlySchedule(tasks, date);
    }

    /** Requests a full schedule.
        @return  Full schedule. */
    public List<Task> getFullSchedule() {
        return scheduler.fullSchedule(tasks);
    }

    /** Writes a schedule to a file.
        @param  schedule The schedule to be written.
        @param  fileName The name of the file to write to. */
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

    /** Reads a schedule from a given file.
        @param  fileName The name of the file holding a valid schedule in JSON format. */
    public void readScheduleFromFile(String fileName) throws IOException {
        List<Task> backup = new ArrayList<Task>(); // Stores a copy in case reading fails
        for (Task task : tasks) {
            backup.add(task);
        }

        boolean created = false;
        isReading = true;

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
                    throw new IllegalArgumentException("Error: File contains task \"" + name + "\" with an invalid time: " + startTime.toString());
                }
                if (!(duration >= 0.25 && duration <= 23.75 && duration % 0.25 == 0)) {
                    throw new IllegalArgumentException("Error: File contains task \"" + name + "\" with an invalid duration: " + duration.toString());
                }

                type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();

                // Check which type of task the JSON object represents by type
                if (type.equals("Visit") || type.equals("Shopping") || type.equals("Appointment")) {
                    Integer date = extractInt(json, "Date");
                    if (!calendar.isValidDate(date)) {
                        throw new IllegalArgumentException("Error: File contains task \"" + name + "\" with an invalid date: " + date.toString());
                    }
                    created = createTask(new TransientTask(name, type, startTime, duration, date));
                    if (!created) {
                        break;
                    }
                } else if (type.equals("Class") || type.equals("Study") || type.equals("Sleep") 
                            || type.equals("Exercise") || type.equals("Work") || type.equals("Meal")) {
                    Integer startDate = extractInt(json, "StartDate");
                    Integer endDate = extractInt(json, "EndDate");
                    Integer frequency = extractInt(json, "Frequency");
                    if (!calendar.isValidDate(startDate)) {
                        throw new IllegalArgumentException("Error: File contains task \"" + name + "\" with an invalid start date: " + startDate.toString());
                    }
                    if (!calendar.isValidDate(endDate)) {
                        throw new IllegalArgumentException("Error: File contains task \"" + name + "\" with an invalid end date: " + endDate.toString());
                    }
                    if (startDate > endDate) {
                        throw new IllegalArgumentException("Error: File contains task \"" + name + "\" with an invalid date period: " + startDate.toString() + " - " + endDate.toString());
                    }
                    if (frequency < 0 || frequency > 7) {
                        throw new IllegalArgumentException("Error: File contains task \"" + name + "\" with an invalid frequency: " + frequency.toString());
                    }
                    created = createTask(new RecurringTask(name, type, startTime, duration, startDate, endDate, frequency));
                    if (!created) {
                        break;
                    }
                } else if (type.equals("Cancellation") ){
                    Integer date = extractInt(json, "Date");
                    if (!calendar.isValidDate(date)) {
                        throw new IllegalArgumentException("Error: File contains task \"" + name + "\" with an invalid date: " + date.toString());
                    }
                    created = createTask(new AntiTask(name, type, startTime, duration, date));
                    if (!created) {
                        break;
                    }
                } else {
                    throw new IllegalArgumentException("Error: File contains task \"" + name + "\" with an invalid type: " + type);
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
            tasks = new ArrayList<Task>(); // If read fails, restore tasks and names
            names = new HashSet<String>();
            for (Task task : backup) {
                tasks.add(task);
                names.add(task.getName());
            }
        } else {
            System.out.println("------------------------------");
            System.out.println("Successfully read schedule");
        }
        isReading = false;
    }

    /** Extracts the string value of a given JSON object by key.
        @param  json The JSON object as a string.
        @param  key The key of the JSON object.
        @return  The string value of a given key. */
    private String extractString(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(json);
        matcher.find();
        return matcher.group(1);
    }

    /** Extracts the floating-point value of a given JSON object by key.
        @param  json The JSON object as a string.
        @param  key The key of the JSON object.
        @return  The floating-point value of a given key. */
    private Double extractDouble(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(json);
        matcher.find();
        return Double.parseDouble(matcher.group(1));
    }

    /** Extracts the integer value of a given JSON object by key.
        @param  json The JSON object as a string.
        @param  key The key of the JSON object.
        @return  The integer value of a given key. */
    private Integer extractInt(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        matcher.find();
        return Integer.parseInt(matcher.group(1));
    }
}
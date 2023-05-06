import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/** Class for handling all user input and actions */
public class Controller {

    private Model model;
    private Viewer viewer;
    private Scanner keyboard;
    private Calendar calendar;

    /** Type of tasks */
    enum Type {
        Class,
        Study,
        Sleep,
        Exercise,
        Work,
        Meal,
        Visit,
        Shopping,
        Appointment,
        Cancellation
    }

    /** Constructor with a given model and viewer */
    public Controller(Model model, Viewer viewer) {
        this.model = model;
        this.viewer = viewer;
        this.keyboard = new Scanner(System.in);
        this.calendar = new Calendar();
    }

    /** Displays the menu selection for all user input. */
    public void menuSelection() {

        int action;

        viewer.displayTitle();

        while (true) {
            try {
                viewer.displayMenu();
                System.out.print("Enter an action (1-8): ");
                action = keyboard.nextInt();
                keyboard.nextLine();
                switch (action) {
                    case 1 -> creatingTask();
                    case 2 -> viewingTask();
                    case 3 -> editingTask();
                    case 4 -> deletingTask();
                    case 5 -> readingScheduleFromFile();
                    case 6 -> writingScheduleToFile();
                    case 7 -> viewingSchedule();
                    case 8 -> exiting();
                    default -> System.out.println("Invalid action");
                }
            } catch (Exception e) {
                System.out.println("Invalid action");
                keyboard.nextLine();
                continue;
            }
        }
    }

    /** Action for creating a task. */
    public void creatingTask() {
        Set<String> names = model.getNames();
        String name;
        String type;
        double startTime;
        double duration;
        int date;
        int endDate;
        int frequency;
        String input;
        Type taskType;
        System.out.println("Creating a task ('-c' anytime to cancel)");
        while (true) {
            System.out.print("Enter name: ");
            name = keyboard.nextLine();
            if (name.equals("-c")) {
                System.out.println("------------------------------");
                System.out.println("Canceled creating a task");
                return;
            } else if (name.trim().isEmpty()) {
                System.out.println("Task name can not be blank");
            } else if (names.contains(name)) {
                System.out.println("Task name must be unique");
            } else {
                break;
            }
        }
        while (true) {
            try {
                System.out.print("Enter type ('-t' for available types): ");
                type = keyboard.nextLine();
                if (type.equals("-c")) {
                    System.out.println("------------------------------");
                    System.out.println("Canceled creating a task");
                    return;
                } else if (type.equals("-t")) {
                    System.out.println(
                            "Available task types: ['Class', 'Study', 'Sleep', 'Exercise', 'Work', 'Meal', 'Visit', 'Shopping', 'Appointment', 'Cancellation']");
                } else {
                    type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                    taskType = Type.valueOf(type);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid task type");
            }
        }
        while (true) {
            try {
                System.out.print("Select start time (0-23.75): ");
                input = keyboard.nextLine();
                if (input.equals("-c")) {
                    System.out.println("------------------------------");
                    System.out.println("Canceled creating a task");
                    return;
                } else {
                    startTime = Double.parseDouble(input);
                    if (startTime >= 0 && startTime <= 23.75 && startTime % 0.25 == 0) {
                        break;
                    } else {
                        System.out.println("Invalid start time input");
                        continue;
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid start time input");
            }
        }
        while (true) {
            try {
                System.out.print("Select duration (0.25-23.75): ");
                input = keyboard.nextLine();
                if (input.equals("-c")) {
                    System.out.println("------------------------------");
                    System.out.println("Canceled creating a task");
                    return;
                } else {
                    duration = Double.parseDouble(input);
                    if (duration >= 0.25 && duration <= 23.75 && duration % 0.25 == 0) {
                        break;
                    } else {
                        System.out.println("Invalid duration input");
                        continue;
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid duration input");
            }
        }
        while (true) {
            try {
                System.out.print("Select date (YYYYMMDD): ");
                input = keyboard.nextLine();
                if (input.equals("-c")) {
                    System.out.println("------------------------------");
                    System.out.println("Canceled creating a task");
                    return;
                } else {
                    date = Integer.parseInt(input);
                    if (calendar.isValidDate(date)) {
                        break;
                    } else {
                        System.out.println("Invalid date input");
                        continue;
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid date input");
            }
        }
        switch (taskType) {
            case Visit, Shopping, Appointment -> {
                model.createTask(new TransientTask(name, type, startTime, duration, date));
            }
            case Class, Study, Sleep, Exercise, Work, Meal -> {
                while (true) {
                    try {
                        System.out.print("Select end date (YYYYMMDD): ");
                        input = keyboard.nextLine();
                        if (input.equals("-c")) {
                            System.out.println("------------------------------");
                            System.out.println("Canceled creating a task");
                            return;
                        } else {
                            endDate = Integer.parseInt(input);
                            if (calendar.isValidDate(endDate) && date < endDate) {
                                break;
                            } else {
                                System.out.println("Invalid end date input");
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid end date input");
                    }
                }
                while (true) {
                    try {
                        System.out.print("Select frequency (1-7): ");
                        input = keyboard.nextLine();
                        if (input.equals("-c")) {
                            System.out.println("------------------------------");
                            System.out.println("Canceled creating a task");
                            return;
                        } else {
                            frequency = Integer.parseInt(input);
                            if (frequency >= 1 && frequency <= 7) {
                                break;
                            } else {
                                System.out.println("Invalid frequency input");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid frequency input");
                    }
                }
                model.createTask(new RecurringTask(name, type, startTime, duration, date, endDate, frequency));
            }
            case Cancellation -> {
                model.createTask(new AntiTask(name, type, startTime, duration, date));
            }
            default -> {
            }
        }
    }

    /** Action for viewing a task. */
    public void viewingTask() {
        String name;
        System.out.println("Viewing a task");
        System.out.print("Enter the task name (-c to cancel): ");
        name = keyboard.nextLine();
        if (name.equals("-c")) {
            System.out.println("------------------------------");
            System.out.println("Canceled viewing a task");
            return;
        }
        Task task = model.viewTask(name);
        if (task == null) {
            System.out.println("------------------------------");
            System.out.println("No tasks found with given name");
        } else {
            System.out.println("---------------");
            viewer.displayTask(task);
        }
    }

    /** Action for editing a task. */
    public void editingTask() {
        Set<String> names = model.getNames();
        String name;
        String type;
        double startTime;
        double duration;
        int date;
        int endDate;
        int frequency;
        String input;
        Type taskType;
        System.out.println("Editing a task");
        System.out.print("Enter the task name (-c to cancel): ");
        name = keyboard.nextLine();
        if (name.equals("-c")) {
            System.out.println("------------------------------");
            System.out.println("Canceled editing a task");
            return;
        }
        Task task = model.viewTask(name);
        if (task == null) {
            System.out.println("------------------------------");
            System.out.println("No tasks found with given name");
        } else {
            taskType = Type.valueOf(task.getType());
            System.out.println("---------------");
            viewer.displayTask(task);
            System.out.println("Edit mode, any fields left blank will not be updated (-c anytime to cancel): ");
            System.out.println("------------------------------");
            while (true) {
                System.out.print("Name: ");
                name = keyboard.nextLine();
                if (name.equals("-c")) {
                    System.out.println("------------------------------");
                    System.out.println("Canceled editing a task");
                    return;
                } else if (names.contains(name) && !task.getName().equals(name)) {
                    System.out.println("Task name must be unique");
                } else if (name.trim().isEmpty()) {
                    name = task.getName();
                    break;
                } else {
                    break;
                }
            }
            while (true) {
                try {
                    System.out.print("Type ('-t' for available types): ");
                    type = keyboard.nextLine();
                    if (type.equals("-c")) {
                        System.out.println("------------------------------");
                        System.out.println("Canceled editing a task");
                        return;
                    } else if (type.equals("-t")) {
                        System.out.println(
                                "Available task types: ['Class', 'Study', 'Sleep', 'Exercise', 'Work', 'Meal', 'Visit', 'Shopping', 'Appointment', 'Cancellation']");
                    } else if (type.trim().isEmpty()) {
                        type = task.getType();
                        break;
                    } else {
                        type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                        taskType = Type.valueOf(type);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid task type");
                }
            }
            while (true) {
                try {
                    System.out.print("Start time (0-23.75): ");
                    input = keyboard.nextLine();
                    if (input.equals("-c")) {
                        System.out.println("------------------------------");
                        System.out.println("Canceled editing a task");
                        return;
                    } else if (input.trim().isEmpty()) {
                        startTime = task.getStartTime();
                        break;
                    } else {
                        startTime = Double.parseDouble(input);
                        if (startTime >= 0 && startTime <= 23.75 && startTime % 0.25 == 0) {
                            break;
                        } else {
                            System.out.println("Invalid start time input");
                            continue;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Invalid start time input");
                }
            }
            while (true) {
                try {
                    System.out.print("Duration (0.25-23.75): ");
                    input = keyboard.nextLine();
                    if (input.equals("-c")) {
                        System.out.println("------------------------------");
                        System.out.println("Canceled editing a task");
                        return;
                    } else if (input.trim().isEmpty()) {
                        duration = task.getDuration();
                        break;
                    } else {
                        duration = Double.parseDouble(input);
                        if (duration >= 0.25 && duration <= 23.75 && duration % 0.25 == 0) {
                            break;
                        } else {
                            System.out.println("Invalid duration input");
                            continue;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Invalid duration input");
                }
            }
            while (true) {
                try {
                    System.out.print("Date (YYYYMMDD): ");
                    input = keyboard.nextLine();
                    if (input.equals("-c")) {
                        System.out.println("------------------------------");
                        System.out.println("Canceled editing a task");
                        return;
                    } else if (input.trim().isEmpty()) {
                        date = task.getDate();
                        break;
                    } else {
                        date = Integer.parseInt(input);
                        if (calendar.isValidDate(date)) {
                            break;
                        } else {
                            System.out.println("Invalid date input");
                            continue;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Invalid date input");
                }
            }
            switch (taskType) {
                case Visit, Shopping, Appointment -> {
                    model.editTask(task, new TransientTask(name, type, startTime, duration, date));
                }
                case Class, Study, Sleep, Exercise, Work, Meal -> {
                    RecurringTask recurringTask;
                    if (task.getClass() == TransientTask.class || task.getClass() == AntiTask.class) {
                        recurringTask = new RecurringTask(name, type, startTime, duration, date, 0, 0);
                    } else {
                        recurringTask = (RecurringTask) task;
                    }
                    while (true) {
                        try {
                            System.out.print("End date (YYYYMMDD): ");
                            input = keyboard.nextLine();
                            if (input.equals("-c")) {
                                System.out.println("------------------------------");
                                System.out.println("Canceled editing a task");
                                return;
                            } else if (input.trim().isEmpty() && task.getClass() == RecurringTask.class) {
                                endDate = recurringTask.getEndDate();
                                break;
                            } else {
                                endDate = Integer.parseInt(input);
                                if (calendar.isValidDate(endDate) && date < endDate) {
                                    break;
                                } else {
                                    System.out.println("Invalid end date input");
                                    continue;
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid end date input");
                        }
                    }
                    while (true) {
                        try {
                            System.out.print("Frequency (1-7): ");
                            input = keyboard.nextLine();
                            if (input.equals("-c")) {
                                System.out.println("------------------------------");
                                System.out.println("Canceled editing a task");
                                return;
                            } else if (input.trim().isEmpty() && task.getClass() == RecurringTask.class) {
                                frequency = recurringTask.getFrequency();
                                break;
                            } else {
                                frequency = Integer.parseInt(input);
                                if (frequency >= 1 && frequency <= 7) {
                                    break;
                                } else {
                                    System.out.println("Invalid frequency input");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid frequency input");
                        }
                    }
                    model.editTask(task, new RecurringTask(name, type, startTime, duration, date, endDate, frequency));
                }
                case Cancellation -> {
                    model.editTask(task, new AntiTask(name, type, startTime, duration, date));
                }
                default -> {
                }
            }
        }
    }

    /** Action for deleting a task. */
    public void deletingTask() {
        String name;
        System.out.println("Deleting a task");
        System.out.print("Enter the task name (-c to cancel): ");
        name = keyboard.nextLine();
        if (name.equals("-c")) {
            System.out.println("------------------------------");
            System.out.println("Canceled deleting a task");
            return;
        }
        model.deleteTask(name);
    }

    /** Action for reading a schedule from a given file. */
    public void readingScheduleFromFile() throws IOException {
        String fileName;
        System.out.println("Reading a schedule from a file");
        System.out.print("Enter File Name (-c to cancel): ");
        fileName = keyboard.nextLine();
        if (fileName.equals("-c")) {
            System.out.println("------------------------------");
            System.out.println("Canceled reading a schedule from a file");
            return;
        }
        File file = new File(fileName);
        if (file.exists()) {
            model.readScheduleFromFile(fileName);
        } else {
            System.out.println("------------------------------");
            System.out.println("Error: File does not exist");
        }
    }

    /** Action for writing a day, week, month, or full schedule to a file. */
    public void writingScheduleToFile() {
        String input;
        int timePeriod;
        int date;
        String fileName;
        System.out.println("Writing a schedule to a file (-c anytime to cancel)");
        while (true) {
            try {
                System.out.println("1) Day");
                System.out.println("2) Week");
                System.out.println("3) Month");
                System.out.println("4) Full");
                System.out.print("Enter time period (1-4): ");
                input = keyboard.nextLine();
                if (input.equals("-c")) {
                    System.out.println("------------------------------");
                    System.out.println("Canceled writing a schedule to a file");
                    return;
                }
                timePeriod = Integer.parseInt(input);
                if (timePeriod < 0 && timePeriod > 4) {
                    System.out.println("Invalid time period");
                    continue;
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid time period");
                continue;
            }

        }
        System.out.print("Enter Output File Name: ");
        input = keyboard.nextLine();
        if (input.equals("-c")) {
            System.out.println("------------------------------");
            System.out.println("Canceled writing a schedule to a file");
            return;
        } else {
            fileName = input;
        }
        while (true) {
            try {
                switch (timePeriod) {
                    case 1 -> {
                        System.out.print("Enter the date: ");
                        input = keyboard.nextLine();
                        if (input.equals("-c")) {
                            System.out.println("------------------------------");
                            System.out.println("Canceled writing a schedule to a file");
                            return;
                        }
                        date = Integer.parseInt(input);
                        if (calendar.isValidDate(date)) {
                            List<Task> schedule = model.getDailySchedule(date);
                            model.writeScheduleToFile(schedule, fileName);
                            return;
                        } else {
                            System.out.println("Invalid date");
                            continue;
                        }
                    }
                    case 2 -> {
                        System.out.print("Enter the first date of the week: ");
                        input = keyboard.nextLine();
                        if (input.equals("-c")) {
                            System.out.println("------------------------------");
                            System.out.println("Canceled writing a schedule to a file");
                            return;
                        }
                        date = Integer.parseInt(input);
                        if (calendar.isValidDate(date)) {
                            List<Task> schedule = model.getWeeklySchedule(date);
                            model.writeScheduleToFile(schedule, fileName);
                            return;
                        } else {
                            System.out.println("Invalid date");
                            continue;
                        }
                    }
                    case 3 -> {
                        System.out.print("Enter the first date of the month: ");
                        input = keyboard.nextLine();
                        if (input.equals("-c")) {
                            System.out.println("------------------------------");
                            System.out.println("Canceled writing a schedule to a file");
                            return;
                        }
                        date = Integer.parseInt(input);
                        if (calendar.isValidDate(date)) {
                            List<Task> schedule = model.getMonthlySchedule(date);
                            model.writeScheduleToFile(schedule, fileName);
                            return;
                        } else {
                            System.out.println("Invalid date");
                            continue;
                        }
                    }
                    case 4 -> {
                        List<Task> schedule = model.getFullSchedule();
                        model.writeScheduleToFile(schedule, fileName);
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid date");
                continue;
            }
        }
    }

    /** Action for viewing a day, week, month, or full schedule. */
    public void viewingSchedule() {
        String input;
        int timePeriod;
        int date;
        System.out.println("Viewing schedule (-c anytime to cancel)");
        while (true) {
            try {
                System.out.println("1) Day");
                System.out.println("2) Week");
                System.out.println("3) Month");
                System.out.println("4) Full");
                System.out.print("Enter time period (1-4): ");
                input = keyboard.nextLine();
                if (input.equals("-c")) {
                    System.out.println("------------------------------");
                    System.out.println("Canceled viewing schedule");
                    return;
                }
                timePeriod = Integer.parseInt(input);
                if (timePeriod < 0 && timePeriod > 4) {
                    System.out.println("Invalid time period");
                    continue;
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid time period");
                continue;
            }

        }
        while (true) {
            try {
                switch (timePeriod) {
                    case 1 -> {
                        System.out.print("Enter the date: ");
                        input = keyboard.nextLine();
                        if (input.equals("-c")) {
                            System.out.println("------------------------------");
                            System.out.println("Canceled viewing schedule");
                            return;
                        }
                        date = Integer.parseInt(input);
                        if (calendar.isValidDate(date)) {
                            List<Task> schedule = model.getDailySchedule(date);
                            viewer.displaySchedule(schedule);
                            return;
                        } else {
                            System.out.println("Invalid date");
                            continue;
                        }
                    }
                    case 2 -> {
                        System.out.print("Enter the first date of the week: ");
                        input = keyboard.nextLine();
                        if (input.equals("-c")) {
                            System.out.println("------------------------------");
                            System.out.println("Canceled viewing schedule");
                            return;
                        }
                        date = Integer.parseInt(input);
                        if (calendar.isValidDate(date)) {
                            List<Task> schedule = model.getWeeklySchedule(date);
                            viewer.displaySchedule(schedule);
                            return;
                        } else {
                            System.out.println("Invalid date");
                            continue;
                        }
                    }
                    case 3 -> {
                        System.out.print("Enter the first date of the month: ");
                        input = keyboard.nextLine();
                        if (input.equals("-c")) {
                            System.out.println("------------------------------");
                            System.out.println("Canceled viewing schedule");
                            return;
                        }
                        date = Integer.parseInt(input);
                        if (calendar.isValidDate(date)) {
                            List<Task> schedule = model.getMonthlySchedule(date);
                            viewer.displaySchedule(schedule);
                            return;
                        } else {
                            System.out.println("Invalid date");
                            continue;
                        }
                    }
                    case 4 -> {
                        List<Task> schedule = model.getFullSchedule();
                        viewer.displaySchedule(schedule);
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid date");
                continue;
            }
        }
    }

    /** Action for exiting a session. */
    public void exiting() {
        System.out.println("Exiting");
        keyboard.close();
        System.exit(1);
    }
}
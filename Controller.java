import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Controller {

    static Model model = new Model();
    static Viewer viewer = new Viewer();

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

    public static void main(String[] args) {

        Scanner keyboard = new Scanner(System.in);
        int action;

        displayTitle();

        while (true) {
            try {
                displayMenu();
                System.out.print("Enter an action (1-8): ");
                action = keyboard.nextInt();
                keyboard.nextLine();
                switch (action) {
                    case 1 -> creatingTask(keyboard);
                    case 2 -> viewingTask(keyboard);
                    case 3 -> editingTask(keyboard);
                    case 4 -> deletingTask(keyboard);
                    case 5 -> readingScheduleFromFile(keyboard);
                    case 6 -> writingScheduleToFile(keyboard);
                    case 7 -> viewingSchedule(keyboard);
                    case 8 ->  {
                        keyboard.close();
                        exiting();
                    }
                    default -> System.out.println("Invalid action");
                }
            } catch (Exception e) {
                System.out.println("Invalid action");
                keyboard.nextLine();
                continue;
            }
        }
    }

    public static void displayTitle() {
        System.out.println(" ___  ___ ___ ");
        System.out.println("| _ \\/ __/ __|");
        System.out.println("|  _/\\__ \\__ \\");
        System.out.println("|_|  |___/___/\n");
    }

    public static void displayMenu() {
        System.out.println("------------------------------");
        System.out.println("1) Create a task");
        System.out.println("2) View a task");
        System.out.println("3) Edit a task");
        System.out.println("4) Delete a task");
        System.out.println("5) Read schedule from a file");
        System.out.println("6) Write schedule to a file");
        System.out.println("7) View schedule");
        System.out.println("8) Exit session");
        System.out.println("------------------------------");
    }

    public static void creatingTask(Scanner keyboard) {
        Map<String, Boolean> names = model.getNames();
        String name;
        String type;
        double time;
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
            } else if (names.containsKey(name)) {
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
                    System.out.println("Available task types: ['Class', 'Study', 'Sleep', 'Exercise', 'Work', 'Meal', 'Visit', 'Shopping', 'Appointment', 'Cancellation']");
                } else {
                    String formattedType = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                    taskType = Type.valueOf(formattedType);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid task type");
            }
        }
        while (true) {
            try {
                System.out.print("Select time (0-23.75): ");
                input = keyboard.nextLine();
                if (input.equals("-c")) {
                    System.out.println("------------------------------");
                    System.out.println("Canceled creating a task");
                    return;
                } else {
                    if (!input.matches("^(0?[0-9]|1[0-9]|2[0-3])(\\.(0|00|25|5|50|75))?$")) {
                        throw new Exception(input, null);
                    }
                    time = Double.parseDouble(input);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid time input");
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
                } else if (Double.parseDouble(input) == 0) {
                    throw new Exception(input, null);
                } else {
                    if (!input.matches("^(0?[0-9]|1[0-9]|2[0-3])(\\.(0|00|25|5|50|75))?$")) {
                        throw new Exception(input, null);
                    }
                    duration = Double.parseDouble(input);
                    break;
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
                    // needs to check if date is valid
                    date = Integer.parseInt(input);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Invalid date input");
            }
        }
        switch (taskType) {
            case Class, Study, Sleep, Exercise, Work, Meal -> {
                // needs to check if there are any overlapping conflicts
                model.createTask(new TransientTask(name, type, time, duration, date));
            }
            case Visit, Shopping, Appointment -> {
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
                            if (date > endDate) { // checks if end date is after start date
                                throw new Exception(input, null);
                            }
                            break;
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
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid frequency input");
                    }
                }
                // needs to check if there are any overlapping conflicts
                model.createTask(new RecursiveTask(name, type, time, duration, date, endDate, frequency));
            }
            case Cancellation -> {}
            default -> {}
        }
    }

    public static void viewingTask(Scanner keyboard) {
        List<Task> taskList = model.getTasks();
        String input;
        Boolean found = false;
        
        System.out.println("Viewing a task");
        System.out.print("Enter the task name: ");
        input = keyboard.nextLine();
        for (Task task : taskList) {
            if (task.getName().equals(input)) {
                found = true;
                task.print();
                break;
            }
        }
        if (!found) {
            System.out.println("No task found with that given name");
        }
    }

    public static void editingTask(Scanner keyboard) {
        System.out.println("Editing a task");
    }

    public static void deletingTask(Scanner keyboard) {
        System.out.println("Deleting a task");
    }

    public static void readingScheduleFromFile(Scanner keyboard) {
        System.out.println("Reading a schedule from a file");
    }

    public static void writingScheduleToFile(Scanner keyboard) {
        System.out.println("Writing a schedule to a file");
    }

    public static void viewingSchedule(Scanner keyboard) {
        System.out.println("Viewing schedule");
        List<Task> taskList = model.getTasks();
        if (taskList.isEmpty()) {
            System.out.println("No tasks found");
        } else {
            viewer.displaySchedule(taskList);
        }
    }

    public static void exiting() {
        System.out.println("Exiting..");
        System.exit(1);
    }
}
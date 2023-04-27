import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Controller {

    private Model model;
    private Viewer viewer;

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

    public Controller(Model model, Viewer viewer) {
        this.model = model;
        this.viewer = viewer;
    }

    public void menuSelection() {

        Scanner keyboard = new Scanner(System.in);
        int action;

        viewer.displayTitle();

        while (true) {
            try {
                viewer.displayMenu();
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


    public void creatingTask(Scanner keyboard) {
        Set<String> names = model.getNames();
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
                    System.out.println("Available task types: ['Class', 'Study', 'Sleep', 'Exercise', 'Work', 'Meal', 'Visit', 'Shopping', 'Appointment', 'Cancellation']");
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

    public void viewingTask(Scanner keyboard) {
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

    public void editingTask(Scanner keyboard) {
        System.out.println("Editing a task");
    }

    public void deletingTask(Scanner keyboard) {
        System.out.println("Deleting a task");
    }

    public void readingScheduleFromFile(Scanner keyboard) {
        System.out.println("Reading a schedule from a file");
    }

    public void writingScheduleToFile(Scanner keyboard) {
        System.out.println("Writing a schedule to a file");
    }

    public void viewingSchedule(Scanner keyboard) {
        System.out.println("Viewing schedule");
        List<Task> taskList = model.getTasks();
        if (taskList.isEmpty()) {
            System.out.println("No tasks found");
        } else {
            viewer.displaySchedule(taskList);
        }
    }

    public void exiting() {
        System.out.println("Exiting..");
        System.exit(1);
    }
}
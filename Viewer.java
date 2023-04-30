import java.util.List;

/** Class for displaying title, menu, task, and schedule */
public class Viewer {

    /** Displays a title. */
    public void displayTitle() {
        System.out.println(" ___  ___ ___ ");
        System.out.println("| _ \\/ __/ __|");
        System.out.println("|  _/\\__ \\__ \\");
        System.out.println("|_|  |___/___/\n");
    }

    /** Displays a menu. */
    public void displayMenu() {
        System.out.println("-------------Menu-------------\n");
        System.out.println("1) Create Task");
        System.out.println("2) View Task");
        System.out.println("3) Edit Task");
        System.out.println("4) Delete Task");
        System.out.println("5) Read Schedule from a File");
        System.out.println("6) Write Schedule to a File");
        System.out.println("7) View Schedule");
        System.out.println("8) Exit Session");
        System.out.println("------------------------------");
    }

    /** Displays information of a given task.
        @param  task The given task. */
    public void displayTask(Task task) {
        task.print();
        System.out.println("---------------");
    }
    
    /** Displays a schedule.
        @param  schedule The given schedule. */
    public void displaySchedule(List<Task> schedule) {
        System.out.println();
        System.out.println("====Schedule====");
        System.out.println();

        // Case there are no tasks
        if (schedule.size() == 0) {
            System.out.println("Error: There are No Tasks");
            return;
            //---- Could throw an Exception here ----
        } else {
            // sort list here. By Date.
            schedule.forEach((task) -> displayTask(task));
        }
    }
}

import java.util.List;

public class Viewer {
    
    public void displaySchedule(List<Task> list) {
        System.out.println();
        System.out.println("====Schedule====");
        System.out.println();

        // Case there are no tasks
        if (list.size() == 0) {
            System.out.println("Error: There are No Tasks");
            //---- Could throw an Exception here ----
        } else {
            // sort list here. By Date.
            list.forEach((task) -> displayTask(task));
        }
    }

    private void displayTask(Task task) {
        task.print();
        System.out.println("---------------");
    }

    public void displayMenu() {
        System.out.println("===== Menu =====\n");
        System.out.println("1) Create Task");
        System.out.println("2) Edit Task");
        System.out.println("3) Delete Task");
        System.out.println("4) Find Task");
        System.out.println("5) Import Schedule");
        System.out.println("6) Export Schedule");
    }
    
}

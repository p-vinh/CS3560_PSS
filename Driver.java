// Where to start and run program
// Conncects to other user required classes
public class Driver {
    public static void main(String[] args) {
        Model model = new Model();
        Viewer viewer = new Viewer();
        Controller controller = new Controller(model, viewer);
        controller.menuSelection();
    }
}

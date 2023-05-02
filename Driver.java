public class Driver {
    public static void main(String[] args) {
        Viewer viewer = new Viewer();
        Model model = new Model();
        Controller controller = new Controller(model, viewer);
        controller.menuSelection();

    }
}

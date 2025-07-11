public class Subtask extends Task {
    Epic epic;

    public Subtask(String name, String description, TaskStatus status, Epic epic) {
        super(name, description, status);
        this.epic = epic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                super.toString() +
                '}';
    }
}

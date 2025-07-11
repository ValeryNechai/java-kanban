import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    public static final HashMap<Integer, Task> tasks = new HashMap<>();
    public static final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public static final HashMap<Integer, Epic> epics = new HashMap<>();


    public int addNewTask(Task task) {
        int idNumber = IDGenerator.getIDNumber();
        tasks.put(idNumber, task);
        return idNumber;
    }

    public int addNewEpic(Epic epic) {
        int idNumber = IDGenerator.getIDNumber();
        epics.put(idNumber, epic);
        return idNumber;
    }

    public int addNewSubtask(Subtask subtask) {
        int idNumber = IDGenerator.getIDNumber();
        subtasks.put(idNumber, subtask);
        Epic epicForSubtask = subtask.epic;
        ArrayList<Integer> subtaskForEpic = epicForSubtask.getIdSubtasks();
        subtaskForEpic.add(idNumber);
        epicForSubtask.setIdSubtasks(subtaskForEpic);
        return idNumber;
    }

    public Task getTask(int idNumber) {
        return tasks.get(idNumber);
    }

    public Epic getEpic(int idNumber) {
        return epics.get(idNumber);
    }

    public Subtask getSubtask(int idNumber) {
        return subtasks.get(idNumber);
    }

    public void updateTask(Task task, Integer idNumber) {
        tasks.put(idNumber, task);
    }

    public void updateSubtask(Subtask subtask, Integer idNumber) {
        subtasks.put(idNumber, subtask);
    }

    public void updateEpic(Epic epic, Integer idNumber) {
        epics.put(idNumber, epic);
    }

    public void updateEpicStatus(Integer idNumber) {
        Epic epic = epics.get(idNumber);
        ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
        if (idSubtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        int statusNew = 0;
        int statusDone = 0;
        int statusAll = 0;
        for (Integer subtask : idSubtasks) {
            Subtask subtask1 = subtasks.get(subtask);
            if (subtask1.getStatus().equals(TaskStatus.NEW)) {
                statusNew++;
                statusAll++;
            } else if (subtask1.getStatus().equals(TaskStatus.DONE)) {
                statusDone++;
                statusAll++;
            } else if (subtask1.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                statusAll++;
            }
        }
        if (statusAll == statusDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (statusAll == statusNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public ArrayList<Subtask> getAllSubtasksForEpic(Epic epic) {
        ArrayList<Subtask> allSubtasksForEpic = new ArrayList<>();
        for (Integer idSubtaskForEpic : epic.getIdSubtasks()) {
            allSubtasksForEpic.add(subtasks.get(idSubtaskForEpic));
        }
        return allSubtasksForEpic;
    }

    public void removeTask(Integer idNumber) {
        tasks.remove(idNumber);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeSubtask(Integer idNumber) {
        subtasks.remove(idNumber);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void removeEpic(Integer idNumber) {
        epics.remove(idNumber);
    }

    public void removeAllEpics() {
        epics.clear();
    }
}





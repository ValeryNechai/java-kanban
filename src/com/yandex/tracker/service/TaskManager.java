package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();


    public int addNewTask(Task task) {
        int idNumber = IDGenerator.getIDNumber();
        task.setIdNumber(idNumber);
        tasks.put(idNumber, task);
        return idNumber;
    }

    public int addNewEpic(Epic epic) {
        int idNumber = IDGenerator.getIDNumber();
        epic.setIdNumber(idNumber);
        epics.put(idNumber, epic);
        return idNumber;
    }

    public int addNewSubtask(Subtask subtask) {
        int idNumber = IDGenerator.getIDNumber();
        subtask.setIdNumber(idNumber);
        subtasks.put(idNumber, subtask);
        Epic epicForSubtask = epics.get(subtask.getEpicID());
        ArrayList<Integer> subtaskForEpic = epicForSubtask.getIdSubtasks();
        subtaskForEpic.add(idNumber);
        updateEpicStatus(subtask.getEpicID());
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

    public void printAllTasks() {
        System.out.println("Список всех задач: ");
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            allTasks.add(new Task(task.getName(), task.getDescription(), task.getStatus()));
        }
        for (Task task : allTasks) {
            System.out.println(task);
        }
    }

    public void printAllSubtasks() {
        System.out.println("Список всех подзадач: ");
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            allSubtasks.add(new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                    subtask.getEpicID()));
        }
        for (Subtask subtask : allSubtasks) {
            System.out.println(subtask);
        }
    }

    public void printAllEpics() {
        System.out.println("Список всех эпиков: ");
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            allEpics.add(new Epic(epic.getName(), epic.getDescription()));
        }
        for (Epic epic : allEpics) {
            System.out.println(epic);
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getIdNumber(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getIdNumber(), subtask);
        updateEpicStatus(subtask.getEpicID());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getIdNumber(), epic);
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
        Subtask subtask = subtasks.get(idNumber);
        Epic epic = epics.get(subtask.getEpicID());
        ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
        idSubtasks.remove(idNumber);
        updateEpicStatus(subtask.getEpicID());
        subtasks.remove(idNumber);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
            idSubtasks.clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public void removeEpic(Integer idNumber) {
        Epic epic = epics.get(idNumber);
        ArrayList<Integer> idSubtasksForEpic = epic.getIdSubtasks();
        for (int i = idSubtasksForEpic.size() - 1; i >= 0; i--) {
            subtasks.remove(i);
        }
        epics.remove(idNumber);
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }
}





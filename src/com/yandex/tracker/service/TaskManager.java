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
        int id = IDGenerator.getID();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int addNewEpic(Epic epic) {
        int id = IDGenerator.getID();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    public int addNewSubtask(Subtask subtask) {
        int id = IDGenerator.getID();
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epicForSubtask = epics.get(subtask.getEpicID());
        ArrayList<Integer> subtaskForEpic = epicForSubtask.getIdSubtasks();
        subtaskForEpic.add(id);
        updateEpicStatus(subtask.getEpicID());
        return id;
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            allTasks.add(new Task(task.getName(), task.getDescription(), task.getStatus()));
        }
        return allTasks;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            allSubtasks.add(new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                    subtask.getEpicID()));
        }
        return allSubtasks;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            allEpics.add(new Epic(epic.getName(), epic.getDescription()));
        }
        return allEpics;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicID());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateEpicStatus(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
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
    }

    public ArrayList<Subtask> getAllSubtasksForEpic(Epic epic) {
        ArrayList<Subtask> allSubtasksForEpic = new ArrayList<>();
        for (Integer idSubtaskForEpic : epic.getIdSubtasks()) {
            allSubtasksForEpic.add(subtasks.get(idSubtaskForEpic));
        }
        return allSubtasksForEpic;
    }

    public void removeTask(Integer id) {
        tasks.remove(id);
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeSubtask(Integer id) {
        Subtask removedValue = subtasks.remove(id);
        if (removedValue != null) {
            Epic epic = epics.get(removedValue.getEpicID());
            ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
            idSubtasks.remove(id);
            updateEpicStatus(removedValue.getEpicID());
        }
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
            idSubtasks.clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public void removeEpic(Integer id) {
        Epic removedValue = epics.remove(id);
        if (removedValue != null) {
            ArrayList<Integer> idSubtasksForEpic = removedValue.getIdSubtasks();
            for (Integer idSubtasks : idSubtasksForEpic) {
                subtasks.remove(idSubtasks);
            }
        }
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }
}





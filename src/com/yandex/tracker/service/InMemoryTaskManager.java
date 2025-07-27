package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private ArrayList<Task> history = new ArrayList<>(10);


    @Override
    public int addNewTask(Task task) {
        int id = IDGenerator.getID();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = IDGenerator.getID();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
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

    @Override
    public Task getTask(int id) {
        if (history.size() <10) {
            history.add(tasks.get(id));
        } else {
            history.remove(0);
            history.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (history.size() <10) {
            history.add(epics.get(id));
        } else {
            history.remove(0);
            history.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        if (history.size() <10) {
            history.add(subtasks.get(id));
        } else {
            history.remove(0);
            history.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            allTasks.add(new Task(task.getName(), task.getDescription(), task.getStatus()));
        }
        return allTasks;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            allSubtasks.add(new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                    subtask.getEpicID()));
        }
        return allSubtasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Epic epic : epics.values()) {
            allEpics.add(new Epic(epic.getName(), epic.getDescription()));
        }
        return allEpics;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicID());
    }

    @Override
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

    @Override
    public ArrayList<Subtask> getAllSubtasksForEpic(Epic epic) {
        ArrayList<Subtask> allSubtasksForEpic = new ArrayList<>();
        for (Integer idSubtaskForEpic : epic.getIdSubtasks()) {
            allSubtasksForEpic.add(subtasks.get(idSubtaskForEpic));
        }
        return allSubtasksForEpic;
    }

    @Override
    public void removeTask(Integer id) {
        tasks.remove(id);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeSubtask(Integer id) {
        Subtask removedValue = subtasks.remove(id);
        if (removedValue != null) {
            Epic epic = epics.get(removedValue.getEpicID());
            ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
            idSubtasks.remove(id);
            updateEpicStatus(removedValue.getEpicID());
        }
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
            idSubtasks.clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void removeEpic(Integer id) {
        Epic removedValue = epics.remove(id);
        if (removedValue != null) {
            ArrayList<Integer> idSubtasksForEpic = removedValue.getIdSubtasks();
            for (Integer idSubtasks : idSubtasksForEpic) {
                subtasks.remove(idSubtasks);
            }
        }
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
        public ArrayList<Task> getHistory() {
        return history;
    }
}





package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();

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

        if (subtask == null) {
            throw new IllegalArgumentException("Подзадача не может быть null.");
        }

        Epic epic = epics.get(subtask.getEpicID());

        if (epic == null) {
            throw new IllegalArgumentException("Эпик не найден.");
        }

        subtask.setId(id);
        if (subtask.getId() == epic.getId()) {
            throw new IllegalArgumentException("Нельзя добавить подзадачу в саму себя.");
        }

        subtasks.put(id, subtask);
        Epic epicForSubtask = epics.get(subtask.getEpicID());
        ArrayList<Integer> subtaskForEpic = epicForSubtask.getIdSubtasks();
        subtaskForEpic.add(id);
        updateEpicStatus(subtask.getEpicID());
        return id;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
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
            allEpics.add(new Epic(epic.getName(), epic.getDescription(), epic.getIdSubtasks()));
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

    @Override
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
        historyManager.remove(id);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        for (Task task : tasks.values()) {
            int id = task.getId();
            historyManager.remove(id);
        }
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
        historyManager.remove(id);
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
            idSubtasks.clear();
            epic.setStatus(TaskStatus.NEW);
        }
        for (Subtask subtask : subtasks.values()) {
            int id = subtask.getId();
            historyManager.remove(id);
        }
    }

    @Override
    public void removeEpic(Integer id) {
        Epic removedValue = epics.remove(id);
        if (removedValue != null) {
            ArrayList<Integer> idSubtasksForEpic = removedValue.getIdSubtasks();
            for (Integer idSubtasks : idSubtasksForEpic) {
                subtasks.remove(idSubtasks);
                historyManager.remove(idSubtasks);
            }
        }
        historyManager.remove(id);
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
        for (Epic epic : epics.values()) {
            int id = epic.getId();
            historyManager.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}





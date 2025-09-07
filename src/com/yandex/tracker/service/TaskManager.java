package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubtask(Subtask subtask);

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    List<Task> getPrioritizedTasks();

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void updateEpicStatus(Integer id);

    ArrayList<Subtask> getAllSubtasksForEpic(Epic epic);

    void removeTask(Integer id);

    void removeAllTasks();

    void removeSubtask(Integer id);

    void removeAllSubtasks();

    void removeEpic(Integer id);

    void removeAllEpics();

    boolean checkingTaskTimesCross();

    List<Task> getHistory();
}

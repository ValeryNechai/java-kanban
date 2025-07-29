package com.yandex.tracker.service;

import com.yandex.tracker.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int MAX_SIZE = 10;

    private final ArrayList<Task> history = new ArrayList<>(MAX_SIZE);


    @Override
    public void add(Task task) {
        if (history.size() < MAX_SIZE) {
            history.add(new Task(task));
        } else {
            history.remove(0);
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}

package com.yandex.tracker.service;

import com.yandex.tracker.model.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_SIZE = 10;

    private final LinkedList<Task> history = new LinkedList<>();


    @Override
    public void add(Task task) {
        if (task != null) {
            history.add(new Task(task));
        /* делаю копию задачи для того, чтобы в случае изменения каких-то аргументов задачи, в истории сохранялись
        предыдущие версии задачи (версия в момент просмотра) */
        }
        if (history.size() >= MAX_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public LinkedList<Task> getHistory() {
        return new LinkedList<>(history);
    }
}

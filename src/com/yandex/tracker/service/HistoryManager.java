package com.yandex.tracker.service;

import com.yandex.tracker.model.Task;

import java.util.LinkedList;

public interface HistoryManager {

    void add(Task task); //Помечает задачи как просмотренные

    LinkedList<Task> getHistory();
}

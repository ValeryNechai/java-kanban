package com.yandex.tracker.model;

import com.yandex.tracker.service.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> idSubtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public ArrayList<Integer> getIdSubtasks() {
        return idSubtasks;
    }

    @Override
    public String toString() {
        return "com.yandex.tracker.model.Epic{" + super.toString() +
                "subtask=" + idSubtasks +
                '}';
    }
}

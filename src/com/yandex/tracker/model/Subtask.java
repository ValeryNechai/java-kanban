package com.yandex.tracker.model;

import com.yandex.tracker.service.TaskStatus;

public class Subtask extends Task {
    private int epicID;

    public Subtask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "com.yandex.tracker.model.Subtask{" +
                super.toString() +
                '}';
    }
}

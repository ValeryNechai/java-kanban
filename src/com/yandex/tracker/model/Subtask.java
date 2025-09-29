package com.yandex.tracker.model;

import com.yandex.tracker.service.TaskStatus;
import com.yandex.tracker.service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicID;

    public Subtask(String name, String description, TaskStatus status, Duration duration, int epicID) {
        super(name, description, status, duration);
        this.epicID = epicID;
    }

    public Subtask(int id, String name, String description, TaskStatus status, LocalDateTime startTime,
                   Duration duration, int epicID) {
        super(id, name, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, TaskStatus status, LocalDateTime startTime,
                   Duration duration, int epicID) {
        super(name, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return super.toString() + epicID;
    }
}

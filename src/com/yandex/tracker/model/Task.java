package com.yandex.tracker.model;

import com.yandex.tracker.service.TaskStatus;
import com.yandex.tracker.service.TaskType;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private TaskStatus status;
    private int id;
    private TaskType taskType;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task не может быть null");
        }
        this.name = task.name;
        this.description = task.description;
        this.status = task.status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
    }

    @Override
    public String toString() {
        return id + "," + getTaskType() + "," + name + "," + status + "," + description + ",";
    }
}

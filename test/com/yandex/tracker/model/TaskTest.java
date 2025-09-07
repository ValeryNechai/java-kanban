package com.yandex.tracker.model;

import com.yandex.tracker.service.IDGenerator;
import com.yandex.tracker.service.InMemoryTaskManager;
import com.yandex.tracker.service.TaskManager;
import com.yandex.tracker.service.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
class TaskTest {

    @Test
    public void shouldGiveSameTaskWithSameId() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Задача", "Описание задачи", TaskStatus.NEW, Duration.ofMinutes(15));
        int id1 = taskManager.addNewTask(task1);
        int id2 = id1;

        assertEquals(taskManager.getTask(id1), taskManager.getTask(id2),
                "Задачи с одинаковым id должны быть равны друг другу.");
    }
}

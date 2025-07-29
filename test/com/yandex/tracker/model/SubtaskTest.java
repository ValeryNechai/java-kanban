package com.yandex.tracker.model;

import com.yandex.tracker.service.InMemoryTaskManager;
import com.yandex.tracker.service.TaskManager;
import com.yandex.tracker.service.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void shouldGiveSameSubtaskWithSameId() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW, epicId);
        int subtaskId1;
        subtaskId1 = taskManager.addNewSubtask(subtask1);
        int subtaskId2 = subtaskId1;

        assertEquals(taskManager.getSubtask(subtaskId1), taskManager.getSubtask(subtaskId2),
                "Подзадачи с одинаковым id должны быть равны друг другу.");
    }
}
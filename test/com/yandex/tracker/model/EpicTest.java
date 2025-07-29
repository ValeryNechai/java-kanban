package com.yandex.tracker.model;

import com.yandex.tracker.service.InMemoryTaskManager;
import com.yandex.tracker.service.TaskManager;
import com.yandex.tracker.service.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    public void shouldGiveSameEpicWithSameId() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId1 = taskManager.addNewEpic(epic1);
        int epicId2 = epicId1;

        assertEquals(taskManager.getEpic(epicId1), taskManager.getEpic(epicId2),
                "Эпики с одинаковым id должны быть равны друг другу.");
    }
}
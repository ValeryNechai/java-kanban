package com.yandex.tracker.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class ManagersTest {

    @Test
    public void mustCreateNewTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager не создан.");
    }

    @Test
    public void mustCreateNewHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не создан.");
    }
}
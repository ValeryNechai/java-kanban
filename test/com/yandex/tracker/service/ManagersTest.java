package com.yandex.tracker.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
class ManagersTest {
    private Path testFile;

    @BeforeEach
    public void beforeEach() {
        try {
            testFile = Files.createTempFile("test", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void mustCreateNewTaskManager() {
        TaskManager taskManager = Managers.getDefault(testFile.toFile());
        assertNotNull(taskManager, "TaskManager не создан.");
    }

    @Test
    public void mustCreateNewHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не создан.");
    }

    @AfterEach
    public void afterEach() throws IOException {
        Files.deleteIfExists(testFile);
    }
}
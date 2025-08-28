package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private Path testFile;
    private FileBackedTaskManager fb;

    @BeforeEach
    public void beforeEach() {
        try {
            testFile = Files.createTempFile("test", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fb = new FileBackedTaskManager(testFile.toFile());
    }

    @Test
    public void testSaveAndLoadEmptyFile() {
        fb.save();
        FileBackedTaskManager fbtm = new FileBackedTaskManager(testFile.toFile());

        assertTrue(fbtm.getAllTasks().isEmpty(), "Список должен быть пуст.");
        assertTrue(fbtm.getAllSubtasks().isEmpty(), "Список должен быть пуст.");
        assertTrue(fbtm.getAllEpics().isEmpty(), "Список должен быть пуст.");
    }

    @Test
    public void testSaveAndLoadFile() {
        Task task11 = new Task("Прогуляться", "сквер возле дома", TaskStatus.NEW);
        final int taskID11 = fb.addNewTask(task11);

        fb.save();

        assertEquals(1, fb.getAllTasks().size(), "После добавления задачи, количество задач должно " +
                "увеличиваться на единицу.");

        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile(testFile.toFile());

        assertEquals(1, fbtm.getAllTasks().size(), "Количество задач должно совпадать с сохраненным файлом.");
    }

    @AfterEach
    public void afterEach() throws IOException {
        Files.deleteIfExists(testFile);
    }
}

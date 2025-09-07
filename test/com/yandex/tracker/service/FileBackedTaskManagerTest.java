package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.Month.AUGUST;
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
        Task task11 = new Task("Прогуляться", "сквер возле дома", TaskStatus.NEW, Duration.ofMinutes(15));
        fb.addNewTask(task11);
        Epic epic10 = new Epic("Ремонт", "Обновить цвет стен");
        int epicID10 = fb.addNewEpic(epic10);
        Subtask subtask10 = new Subtask("Купить краску", "Бежевого цвета", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 15, 15), Duration.ofMinutes(50),
                epicID10);
        Subtask subtask11 = new Subtask("Купить кисть", "Большую", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 17, 15), Duration.ofMinutes(50),
                epicID10);
        Subtask subtask12 = new Subtask("Покрасить", "Аккуратно", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 19, 15), Duration.ofMinutes(50),
                epicID10);
        fb.addNewSubtask(subtask10);
        fb.addNewSubtask(subtask11);
        fb.addNewSubtask(subtask12);

        fb.save();

        assertEquals(1, fb.getAllTasks().size(), "После добавления задачи, количество задач должно " +
                "увеличиваться на единицу.");
        assertEquals(1, fb.getAllEpics().size(), "После добавления задачи, количество задач должно " +
                "увеличиваться на единицу.");
        assertEquals(3, fb.getAllSubtasks().size(), "После добавления задачи, количество задач должно " +
                "увеличиваться на единицу.");
    }

    @AfterEach
    public void afterEach() throws IOException {
        Files.deleteIfExists(testFile);
    }
}

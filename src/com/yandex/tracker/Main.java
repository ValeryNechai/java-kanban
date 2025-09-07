package com.yandex.tracker;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.service.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.Month.AUGUST;

public class Main {

    public static void main(String[] args) throws IOException {

        String home = System.getProperty("user.home");
        Path testFile = Paths.get(home, "file.txt");
        if (!Files.exists(testFile)) {
            Files.createFile(testFile);
        } else {
            System.out.println("Файл " + testFile.getFileName() + " уже существует.");
        }
        TaskManager taskManager = Managers.getDefault(testFile.toFile());

        Task task11 = new Task("Прогуляться", "сквер возле дома", TaskStatus.NEW,
                Duration.ofMinutes(50));
        Task task12 = new Task("Сходить в магазин", "купить овощи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 12, 15), Duration.ofMinutes(20));
        final int taskID11 = taskManager.addNewTask(task11);
        final int taskID12 = taskManager.addNewTask(task12);

        Epic epic10 = new Epic("Ремонт", "Обновить цвет стен");
        Epic epic11 = new Epic("Подготовка отчета", "Организовать сбор информации");
        final int epicID10 = taskManager.addNewEpic(epic10);
        final int epicID11 = taskManager.addNewEpic(epic11);

        Subtask subtask10 = new Subtask("Купить краску", "Бежевого цвета", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 26, 15, 15), Duration.ofMinutes(50), epicID10);
        Subtask subtask11 = new Subtask("Купить кисть", "Большую", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, AUGUST, 26, 17, 15), Duration.ofMinutes(50),
                epicID10);
        Subtask subtask12 = new Subtask("Покрасить", "Аккуратно", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 26, 18, 15), Duration.ofMinutes(45),
                epicID10);
        taskManager.addNewSubtask(subtask10);
        taskManager.addNewSubtask(subtask11);
        taskManager.addNewSubtask(subtask12);
        taskManager.updateEpicStatus(epicID10);

        System.out.println("Задачи успешно созданы и сохранены!");
        System.out.println("Файл: " + testFile);
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasksForEpic(epic10));
        System.out.println(taskManager.getPrioritizedTasks());
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task subtaskForEpic : manager.getAllSubtasksForEpic((Epic) epic)) {
                System.out.println("--> " + subtaskForEpic);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

}


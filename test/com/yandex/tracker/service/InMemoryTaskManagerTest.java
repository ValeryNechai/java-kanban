package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.time.Month.AUGUST;
import static org.junit.jupiter.api.Assertions.*;
class InMemoryTaskManagerTest {

    public static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldAddNewTask() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW, Duration.ofMinutes(5));
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldAddNewSubtask() {
        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 15, 15), Duration.ofMinutes(50), epicId);
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW, Duration.ofMinutes(5));
        final int taskId = taskManager.addNewTask(task);

        final int subtaskId = taskManager.addNewSubtask(subtask1);
        final Task savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask1, savedSubtask, "Задачи не совпадают.");

        final ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают.");

        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Задачи без времени начала не должны добавляться.");

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addNewSubtask(new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW,
                    LocalDateTime.of(2025, AUGUST, 25, 15, 15), Duration.ofMinutes(50), 5));
        }, "Привязка к несуществующему эпику должна приводить к ошибке");

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addNewSubtask(null);
        }, "Добавление задачи null должно приводить к ошибке");

    }

    @Test
    public void shouldAddNewEpic() {
        Epic epic1 = new Epic("Эпик", "Описание эпика");
        final int epicId = taskManager.addNewEpic(epic1);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");

        final ArrayList<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic1, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldNotAddSubtaskInSubtask() {
        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 15, 15), Duration.ofMinutes(50), epicId);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 17, 15), Duration.ofMinutes(50), subtask1Id);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addNewSubtask(subtask2);
        }, "Добавление задачи самой в себя должно приводить к ошибке");
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW, Duration.ofMinutes(15));
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus(), "Задача не обновлена.");
    }

    @Test
    public void shouldUpdateEpicStatus() {
        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId1 = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 15, 15), Duration.ofMinutes(50), epicId1);
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 17, 15), Duration.ofMinutes(50), epicId1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic1.getStatus(), "Задача не обновлена.");

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Задача не обновлена.");

        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic1.getStatus(), "Задача не обновлена.");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Задача не обновлена.");
    }

    @Test
    public void shouldRemoveEpic() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика");
        final int epic1Id = taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика");
        final int epic2Id = taskManager.addNewEpic(epic2);
        Epic epic3 = new Epic("Эпик 3", "Описание эпика");
        final int epic3Id = taskManager.addNewEpic(epic3);

        ArrayList<Epic> epics = taskManager.getAllEpics();

        assertEquals(3, epics.size(), "Количество задач не совпадает.");

        taskManager.removeEpic(epic1Id);
        ArrayList<Epic> newEpics = taskManager.getAllEpics();

        assertEquals(2, newEpics.size(), "Количество задач не совпадает.");
        assertEquals("Эпик 2", newEpics.get(0).getName(), "Имя задачи не совпадает.");
    }

    @Test
    public void shouldUpdateEpicTimeAndDuration() {
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

        assertEquals(145, epic10.getDuration().toMinutes(),
                "Промежуток от начала первого сабтаска до окончания последнего не совпадает");
    }

    @Test
    public void shouldCheckingTaskTimeCross() {
        Task task1 = new Task("Сходить в магазин", "купить овощи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 12, 15), Duration.ofMinutes(20));
        Task task2 = new Task("Сходить в магазин", "купить овощи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 12, 20), Duration.ofMinutes(20));
        taskManager.addNewTask(task1);

        assertFalse(taskManager.checkingTaskTimesCross(), "Пересечений нет, должен быть false");

        taskManager.addNewTask(task2);

        assertTrue(taskManager.checkingTaskTimesCross(), "Пересечения есть, должен быть true");
    }
}
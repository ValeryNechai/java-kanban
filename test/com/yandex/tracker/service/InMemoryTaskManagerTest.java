package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
class InMemoryTaskManagerTest {

    public static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldAddNewTask() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
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
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW, epicId);

        final int subtaskId = taskManager.addNewSubtask(subtask1);
        final Task savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask1, savedSubtask, "Задачи не совпадают.");

        final ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают.");

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
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epicId);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, subtask1Id);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addNewSubtask(subtask2);
        });
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus(), "Задача не обновлена.");
    }

    @Test
    public void shouldUpdateEpicStatus() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epicId);
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epicId);
        taskManager.addNewSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Задача не обновлена.");

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Задача не обновлена.");
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
}
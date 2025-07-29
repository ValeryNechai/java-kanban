package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    public static HistoryManager historyManager;
    public static ArrayList<Task> history;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        history = historyManager.getHistory();
    }

    @Test
    public void shouldAddTask() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        historyManager.add(task);

        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");

        task.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.add(task);

        assertEquals(2, history.size(), "После добавления задачи, история не должна быть пустой.");
        assertEquals(TaskStatus.NEW, historyManager.getHistory().get(0).getStatus(),
                "После обновления статуса задачи, предыдущая версия данных должна сохраняться.");
    }

    @Test
    public void shouldAddEpic() {
        Task epic1 = new Epic("Эпик", "Описание эпика");
        historyManager.add(epic1);

        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");

        epic1.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.add(epic1);
        assertEquals(TaskStatus.NEW, historyManager.getHistory().get(0).getStatus(),
                "После обновления статуса задачи, предыдущая версия данных должна сохраняться.");
    }

    @Test
    public void shouldAddSubtask() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task epic1 = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addNewEpic((Epic) epic1);
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW, epicId);
        historyManager.add(subtask1);

        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.add(subtask1);
        assertEquals(TaskStatus.NEW, historyManager.getHistory().get(0).getStatus(),
                "После обновления статуса задачи, предыдущая версия данных должна сохраняться.");
    }
}
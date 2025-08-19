package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    public static InMemoryHistoryManager historyManager;
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
        history = historyManager.getHistory();

        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");

        task.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.add(task);

        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
        assertEquals(TaskStatus.IN_PROGRESS, historyManager.getHistory().get(0).getStatus(),
                "После обновления статус задачи должен меняться.");
    }

    @Test
    public void shouldAddEpic() {
        Task epic1 = new Epic("Эпик", "Описание эпика");
        historyManager.add(epic1);
        history = historyManager.getHistory();

        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");

        epic1.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.add(epic1);

        assertEquals(TaskStatus.IN_PROGRESS, historyManager.getHistory().get(0).getStatus(),
                "После обновления статус задачи должен меняться.");
    }

    @Test
    public void shouldAddSubtask() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW, epicId);
        historyManager.add(subtask1);
        history = historyManager.getHistory();

        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.add(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, historyManager.getHistory().get(0).getStatus(),
                "После обновления статус задачи должен меняться.");
    }

    @Test
    public void shouldLinkLast() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        historyManager.linkLast(task);
        assertNotNull(historyManager.getHead(), "После добавления первой задачи, ее значение запишется в head.");
        assertNotNull(historyManager.getTail(), "После добавления первой задачи, ее значение запишется в tail.");

        Task task2 = new Task("Задача2", "Описание задачи", TaskStatus.NEW);
        historyManager.linkLast(task2);
        assertNotNull(historyManager.getHead().getNext(), "После добавления новой задачи, ее значение " +
                "запишется в tail, а head.next больше не будет null.");
    }

    @Test
    public void shouldRemoveNode() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        historyManager.add(task);
        int taskId = task.getId();
        historyManager.removeNode(taskId);
        assertNull(historyManager.getTail(), "После удаления единственной задачи, head = teil = null.");
        assertNull(historyManager.getHead(), "После удаления единственной задачи, head = teil = null.");
    }
}
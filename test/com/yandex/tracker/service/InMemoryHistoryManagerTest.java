package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Month.AUGUST;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    public static InMemoryHistoryManager historyManager;
    public static List<Task> history;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        history = historyManager.getHistory();
    }

    @Test
    public void shouldAddTask() {
        assertEquals(0, history.size(), "До добавления задачи, история должна быть пустой.");

        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW, Duration.ofMinutes(15));
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
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 15, 15), Duration.ofMinutes(50), epicId);
        historyManager.add(subtask1);
        history = historyManager.getHistory();

        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.add(subtask1);
        history = historyManager.getHistory();

        assertEquals(TaskStatus.IN_PROGRESS, historyManager.getHistory().get(0).getStatus(),
                "После обновления статус задачи должен меняться.");

        assertEquals(1, history.size(), "После повторного добавления задачи, старая задача должна удалиться.");


    }

    @Test
    public void shouldRemove() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW, Duration.ofMinutes(15));
        historyManager.add(task);
        historyManager.add(task);
        history = historyManager.getHistory();

        assertEquals(1, history.size(), "После повторного добавления задачи, старая задача должна удалиться.");

        int taskId = task.getId();
        historyManager.remove(taskId);
        assertNotNull(historyManager.getTail(), "После удаления единственной задачи, dummyHead все равно остается.");
        assertNotNull(historyManager.getHead(), "После удаления единственной задачи, dummyTeil все равно остается.");
    }
}
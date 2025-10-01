package com.yandex.tracker.server;

import com.google.gson.Gson;
import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.service.InMemoryTaskManager;
import com.yandex.tracker.service.TaskManager;
import com.yandex.tracker.service.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Month.AUGUST;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtasksTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = taskServer.getGson();
        client = HttpClient.newHttpClient();

        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        taskServer.start(8080);
        Thread.sleep(1000);

        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId = manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 15, 15), Duration.ofMinutes(50), epicId);
        Subtask subtask2 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 15, 20), Duration.ofMinutes(50), epicId);
        String subtaskJson = gson.toJson(subtask1);
        String subtaskJson2 = gson.toJson(subtask2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа отличается от нужного");

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson2))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode(), "Задачи не должны пересекаться!");

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Подзадача", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testRemoveSubtask() throws IOException, InterruptedException {
        taskServer.start(8081);
        Thread.sleep(1000);

        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId = manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 15, 15), Duration.ofMinutes(50), epicId);
        manager.addNewSubtask(subtask1);

        URI url = URI.create("http://localhost:8081/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertEquals(0, subtasksFromManager.size(), "Задачи удаляются.");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        taskServer.start(8082);
        Thread.sleep(1000);

        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId = manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 15, 15), Duration.ofMinutes(50), epicId);
        manager.addNewSubtask(subtask1);

        URI url = URI.create("http://localhost:8082/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        URI url2 = URI.create("http://localhost:8082/subtasks/999");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response2.statusCode(), "Такого id не существует -> 404");
    }
}
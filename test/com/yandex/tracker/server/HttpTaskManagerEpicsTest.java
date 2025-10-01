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

public class HttpTaskManagerEpicsTest {

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
    public void testAddEpic() throws IOException, InterruptedException {
        taskServer.start(8080);
        Thread.sleep(1000);

        Epic epic1 = new Epic("Эпик", "Описание эпика");
        String epicJson = gson.toJson(epic1);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа должен быть 201");

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Эпик", epicsFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testRemoveEpic() throws IOException, InterruptedException {
        taskServer.start(8081);
        Thread.sleep(1000);

        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId = manager.addNewEpic(epic1);

        URI url = URI.create("http://localhost:8081/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertEquals(0, epicsFromManager.size(), "Задачи удаляются.");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        taskServer.start(8082);
        Thread.sleep(1000);

        Epic epic1 = new Epic("Эпик", "Описание эпика");
        int epicId = manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW,
                LocalDateTime.of(2025, AUGUST, 25, 15, 15), Duration.ofMinutes(50), epicId);
        manager.addNewSubtask(subtask1);

        URI url = URI.create("http://localhost:8082/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        URI url2 = URI.create("http://localhost:8082/epics/999");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response2.statusCode(), "Такого id не существует -> 404");

        URI url3 = URI.create("http://localhost:8082/epics/" + epicId + "/subtasks");
        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url3)
                .GET()
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response3.statusCode(), "Код ответа должен быть 200, должен возращаться список сабтасков");
    }
}

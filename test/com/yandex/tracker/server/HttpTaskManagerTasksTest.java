package com.yandex.tracker.server;

import com.google.gson.Gson;
import com.yandex.tracker.model.Task;
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

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

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
    public void shutDown() throws InterruptedException {
        taskServer.stop();
        Thread.sleep(500);
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        taskServer.start(8080);
        Thread.sleep(1000);

        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Задача2", "Описание задачи", TaskStatus.NEW, LocalDateTime.now().plusMinutes(2), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);
        String taskJson2 = gson.toJson(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа отличается от нужного");

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response2.statusCode(), "Задачи не должны пересекаться!");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testRemoveTask() throws IOException, InterruptedException {
        taskServer.start(8081);
        Thread.sleep(1000);

        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addNewTask(task);

        URI url = URI.create("http://localhost:8081/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        List<Task> tasksFromManager = manager.getAllTasks();

        assertEquals(0, tasksFromManager.size(), "Задачи удаляются.");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        taskServer.start(8082);
        Thread.sleep(1000);

        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.addNewTask(task);

        URI url = URI.create("http://localhost:8082/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа должен быть 200");

        URI url2 = URI.create("http://localhost:8082/tasks/5");
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url2)
                .GET()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response2.statusCode(), "Такого id не существует -> 404");
    }
}

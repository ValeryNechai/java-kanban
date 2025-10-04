package com.yandex.tracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.service.ManagerNotFoundException;
import com.yandex.tracker.service.ManagerSaveException;
import com.yandex.tracker.service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetTask(exchange);
                break;
            case "POST":
                handlePostTask(exchange);
                break;
            case "DELETE":
                handleDeleteTask(exchange);
                break;
            default:
                writeResponse(exchange, gson, "Такого эндпоинта не существует.", 404);
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskId = getTaskId(exchange);
        Map<String,String> error = Map.of();
        Task task = null;
        List<Task> tasks = new ArrayList<>();
        if (taskId.isEmpty()) {
            tasks = taskManager.getAllTasks();
        } else if (taskId.get() == -1) {
            error = Map.of("Error_400", "Некорректный идентификатор задачи!");
        } else {
            try {
                task = taskManager.getTask(taskId.get());
            } catch (ManagerNotFoundException exc) {
                error = Map.of("Error_404", exc.getMessage());
            }
        }
        sendGetResponse(exchange, gson, error, task, tasks);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String jsonTaskStr = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(jsonTaskStr, Task.class);
        Map<String,String> error = Map.of();
        Map<String,String> message = Map.of();
        try {
            if (task.getId() == null) {
                if (taskManager.checkingTaskTimesCross(task)) {
                    error = Map.of("Error_406", "Задача пересекается с существующими!");
                } else {
                    taskManager.addNewTask(task);
                    message = Map.of("Message", "Задача успешно добавлена.");
                }
            } else {
                if (taskManager.checkingTaskTimesCross(task)) {
                    error = Map.of("Error_406", "Задача пересекается с существующими!");
                } else {
                    taskManager.updateTask(task);
                    message = Map.of("Message", "Задача успешно обновлена.");
                }
            }
        } catch (ManagerSaveException exc) {
            error = Map.of("Error_500", exc.getMessage());
        }
        sendPostResponse(exchange, gson, error, message);
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskId = getTaskId(exchange);
        Map<String,String> error = Map.of();
        Map<String,String> message = Map.of();
        try {
            if (taskId.isEmpty()) {
                taskManager.removeAllTasks();
                message = Map.of("Message", "Задачи удалены.");
            } else if (taskId.get() == -1) {
                error = Map.of("Error_400", "Некорректный идентификатор задачи!");
            } else {
                taskManager.removeTask(taskId.get());
                message = Map.of("Message", "Задача успешно удалена.");
            }
        } catch (ManagerSaveException exc) {
            error = Map.of("Error_500", exc.getMessage());
        }
        sendDeleteResponse(exchange, gson, error, message);
    }
}

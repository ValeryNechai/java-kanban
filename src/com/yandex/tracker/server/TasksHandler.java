package com.yandex.tracker.server;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson;

    TasksHandler(TaskManager taskManager, Gson gson) {
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
        if (taskId.isEmpty()) {
            List<Task> tasks = taskManager.getAllTasks();
            writeResponse(exchange, gson, tasks, 200);
        } else if (taskId.get() == -1) {
            Map<String,String> error = Map.of("Error", "Некорректный идентификатор задачи!");
            writeResponse(exchange, gson, error, 400);
        } else {
            try {
                Task task = taskManager.getTask(taskId.get());
                writeResponse(exchange, gson, task, 200);
            } catch (ManagerNotFoundException exc) {
                Map<String,String> error = Map.of("Error", exc.getMessage());
                writeResponse(exchange, gson, error, 404);
            }
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String jsonTaskStr = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Task task = gson.fromJson(jsonTaskStr, Task.class);
        if (task.getId() == null) {
            if (taskManager.checkingTaskTimesCross(task)) {
                Map<String,String> error = Map.of("Error", "Задача пересекается с существующими!");
                writeResponse(exchange, gson, error, 406);
            } else {
                try {
                    taskManager.addNewTask(task);
                } catch (ManagerSaveException exc) {
                    writeResponse(exchange, gson, Map.of("Error", exc.getMessage()), 500);
                }
                Map<String,String> message = Map.of("Message", "Задача успешно добавлена.");
                writeResponse(exchange, gson, message, 201);
            }
        } else {
            if (taskManager.checkingTaskTimesCross(task)) {
                Map<String,String> error = Map.of("Error", "Задача пересекается с существующими!");
                writeResponse(exchange, gson, error, 406);
            } else {
                try {
                    taskManager.updateTask(task);
                } catch (ManagerSaveException exc) {
                    writeResponse(exchange, gson, Map.of("Error", exc.getMessage()), 500);
                }
                Map<String,String> message = Map.of("Message", "Задача успешно обновлена.");
                writeResponse(exchange, gson, message, 201);
            }
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskId = getTaskId(exchange);
        if (taskId.isEmpty()) {
            try {
                taskManager.removeAllTasks();
            } catch (ManagerSaveException exc) {
                writeResponse(exchange, gson, Map.of("Error", exc.getMessage()), 500);
            }
            Map<String,String> message = Map.of("Message", "Задачи удалены.");
            writeResponse(exchange, gson, message, 200);
        } else if (taskId.get() == -1) {
            Map<String,String> error = Map.of("Error", "Некорректный идентификатор задачи!");
            writeResponse(exchange, gson, error, 400);
        } else {
            try {
                taskManager.removeTask(taskId.get());
            } catch (ManagerSaveException exc) {
                writeResponse(exchange, gson, Map.of("Error", exc.getMessage()), 500);
            }
            Map<String,String> message = Map.of("Message", "Задача успешно удалена.");
            writeResponse(exchange, gson, message, 200);
        }
    }
}

package com.yandex.tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.service.ManagerNotFoundException;
import com.yandex.tracker.service.ManagerSaveException;
import com.yandex.tracker.service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson;

    SubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetSubtask(exchange);
                break;
            case "POST":
                handlePostSubtask(exchange);
                break;
            case "DELETE":
                handleDeleteSubtask(exchange);
                break;
            default:
                writeResponse(exchange, gson, "Такого эндпоинта не существует.", 404);
        }
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskId = getTaskId(exchange);
        if (subtaskId.isEmpty()) {
            List<Subtask> subtasks = taskManager.getAllSubtasks();
            writeResponse(exchange, gson, subtasks, 200);
        } else if (subtaskId.get() == -1) {
            Map<String,String> error = Map.of("Error", "Некорректный идентификатор задачи!");
            writeResponse(exchange, gson, error, 400);
        } else {
            try {
                Subtask subtask = taskManager.getSubtask(subtaskId.get());
                writeResponse(exchange, gson, subtask, 200);
            } catch (ManagerNotFoundException exc) {
                Map<String,String> error = Map.of("Error", exc.getMessage());
                writeResponse(exchange, gson, error, 404);
            }
        }
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        String jsonSubtaskStr = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Subtask subtask = gson.fromJson(jsonSubtaskStr, Subtask.class);
        if (subtask.getId() == null) {
            if (taskManager.checkingTaskTimesCross(subtask)) {
                Map<String,String> error = Map.of("Error", "Задача пересекается с существующими!");
                writeResponse(exchange, gson, error, 406);
            } else {
                try {
                    taskManager.addNewSubtask(subtask);
                } catch (ManagerSaveException exc) {
                    writeResponse(exchange, gson, Map.of("Error", exc.getMessage()), 500);
                }
                Map<String,String> message = Map.of("Message", "Задача успешно добавлена.");
                writeResponse(exchange, gson, message, 201);
            }
        } else {
            if (taskManager.checkingTaskTimesCross(subtask)) {
                Map<String,String> error = Map.of("Error", "Задача пересекается с существующими!");
                writeResponse(exchange, gson, error, 406);
            } else {
                try {
                    taskManager.updateSubtask(subtask);
                } catch (ManagerSaveException exc) {
                    writeResponse(exchange, gson, Map.of("Error", exc.getMessage()), 500);
                }
                Map<String,String> message = Map.of("Message", "Задача успешно обновлена.");
                writeResponse(exchange, gson, message, 201);
            }
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskId = getTaskId(exchange);
        if (subtaskId.isEmpty()) {
            try {
                taskManager.removeAllSubtasks();
            } catch (ManagerSaveException exc) {
                writeResponse(exchange, gson, Map.of("Error", exc.getMessage()), 500);
            }
            Map<String,String> message = Map.of("Message", "Задачи удалены.");
            writeResponse(exchange, gson, message, 200);
        } else if (subtaskId.get() == -1) {
            Map<String,String> error = Map.of("Error", "Некорректный идентификатор задачи!");
            writeResponse(exchange, gson, error, 400);
        } else {
            try {
                taskManager.removeSubtask(subtaskId.get());
            } catch (ManagerSaveException exc) {
                writeResponse(exchange, gson, Map.of("Error", exc.getMessage()), 500);
            }
            Map<String,String> message = Map.of("Message", "Задача успешно удалена.");
            writeResponse(exchange, gson, message, 200);
        }
    }
}

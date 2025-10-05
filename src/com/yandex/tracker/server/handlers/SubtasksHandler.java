package com.yandex.tracker.server.handlers;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
        Map<String,String> error = Map.of();
        Subtask subtask = null;
        List<Subtask> subtasks = new ArrayList<>();
        if (subtaskId.isEmpty()) {
            subtasks = taskManager.getAllSubtasks();
        } else if (subtaskId.get() == -1) {
            error = Map.of("Error_400", "Некорректный идентификатор задачи!");
        } else {
            try {
                subtask = taskManager.getSubtask(subtaskId.get());
            } catch (ManagerNotFoundException exc) {
                error = Map.of("Error_404", exc.getMessage());
            }
        }
        sendGetResponse(exchange, gson, error, subtask, subtasks);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        String jsonSubtaskStr = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Subtask subtask = gson.fromJson(jsonSubtaskStr, Subtask.class);
        Map<String,String> error = Map.of();
        Map<String,String> message = Map.of();
        try {
            if (subtask.getId() == null) {
                if (taskManager.checkingTaskTimesCross(subtask)) {
                    error = Map.of("Error_406", "Задача пересекается с существующими!");
                } else {
                    taskManager.addNewSubtask(subtask);
                    message = Map.of("Message", "Задача успешно добавлена.");
                }
            } else {
                if (taskManager.checkingTaskTimesCross(subtask)) {
                    Map.of("Error_406", "Задача пересекается с существующими!");
                } else {
                    taskManager.updateSubtask(subtask);
                    message = Map.of("Message", "Задача успешно обновлена.");
                }
            }
        } catch (ManagerSaveException exc) {
            error = Map.of("Error_500", exc.getMessage());
        }
        sendPostResponse(exchange, gson, error, message);
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskId = getTaskId(exchange);
        Map<String,String> error = Map.of();
        Map<String,String> message = Map.of();
        try {
            if (subtaskId.isEmpty()) {
                taskManager.removeAllSubtasks();
                message = Map.of("Message", "Задачи удалены.");
            } else if (subtaskId.get() == -1) {
                error = Map.of("Error_400", "Некорректный идентификатор задачи!");
            } else {
                taskManager.removeSubtask(subtaskId.get());
                message = Map.of("Message", "Задача успешно удалена.");
            }
        } catch (ManagerSaveException exc) {
            error = Map.of("Error", exc.getMessage());
        }
        sendDeleteResponse(exchange, gson, error, message);
    }
}

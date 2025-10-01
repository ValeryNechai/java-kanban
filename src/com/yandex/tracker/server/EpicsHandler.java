package com.yandex.tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.tracker.model.Epic;
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

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson;

    EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetEpic(exchange);
                break;
            case "POST":
                handlePostEpic(exchange);
                break;
            case "DELETE":
                handleDeleteEpic(exchange);
                break;
            default:
                writeResponse(exchange, gson, "Такого эндпоинта не существует.", 404);
        }
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicId = getTaskId(exchange);
        String[] partsPath = exchange.getRequestURI().getPath().split("/");
        if (epicId.isEmpty()) {
            List<Epic> epics = taskManager.getAllEpics();
            writeResponse(exchange, gson, epics, 200);
        } else if (epicId.get() == -1) {
            Map<String,String> error = Map.of("Error", "Некорректный идентификатор задачи!");
            writeResponse(exchange, gson, error, 400);
        } else if (partsPath.length == 4 && partsPath[3].equals("subtasks")) {
            try {
                List<Subtask> allSubtasksForEpic = taskManager.getAllSubtasksForEpic(epicId.get());
                writeResponse(exchange, gson, allSubtasksForEpic, 200);
            } catch (ManagerNotFoundException exc) {
                Map<String,String> error = Map.of("Error", exc.getMessage());
                writeResponse(exchange, gson, error, 404);
            }
        } else {
            try {
                Epic epic = taskManager.getEpic(epicId.get());
                writeResponse(exchange, gson, epic, 200);
            } catch (ManagerNotFoundException exc) {
                Map<String,String> error = Map.of("Error", exc.getMessage());
                writeResponse(exchange, gson, error, 404);
            }
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String jsonEpicStr = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Epic epic = gson.fromJson(jsonEpicStr, Epic.class);
        if (epic.getId() == null) {
            try {
                taskManager.addNewEpic(epic);
            } catch (ManagerSaveException exc) {
                writeResponse(exchange, gson, Map.of("Error", exc.getMessage()), 500);
            }
            Map<String,String> message = Map.of("Message", "Эпик успешно добавлен.");
            writeResponse(exchange, gson, message, 201);
        } else {
            Map<String,String> error = Map.of("Error", "Эпик с таким id уже существует!");
            writeResponse(exchange, gson, error, 400);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicId = getTaskId(exchange);
        if (epicId.isEmpty()) {
            Map<String,String> error = Map.of("Error", "Не указан id удаляемого эпика!");
            writeResponse(exchange, gson, error, 400);
        } else if (epicId.get() == -1) {
            Map<String,String> error = Map.of("Error", "Некорректный идентификатор задачи!");
            writeResponse(exchange, gson, error, 400);
        } else {
            try {
                taskManager.removeEpic(epicId.get());
            } catch (ManagerSaveException exc) {
                writeResponse(exchange, gson, Map.of("Error", exc.getMessage()), 500);
            }
            Map<String,String> message = Map.of("Message", "Эпик успешно удален.");
            writeResponse(exchange, gson, message, 200);
        }
    }
}

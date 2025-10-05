package com.yandex.tracker.server.handlers;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
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
        Map<String,String> error = Map.of();
        Epic epic = null;
        List<Epic> epics = new ArrayList<>();
        List<Subtask> allSubtasksForEpic = new ArrayList<>();
        try {
            if (epicId.isEmpty()) {
                epics = taskManager.getAllEpics();
            } else if (epicId.get() == -1) {
                error = Map.of("Error_400", "Некорректный идентификатор задачи!");
            } else if (partsPath.length == 4 && partsPath[3].equals("subtasks")) {
                allSubtasksForEpic = taskManager.getAllSubtasksForEpic(epicId.get());
            } else {
                epic = taskManager.getEpic(epicId.get());
            }
        } catch (ManagerNotFoundException exc) {
            error = Map.of("Error_404", exc.getMessage());
        }

        if (allSubtasksForEpic.isEmpty()) {
            sendGetResponse(exchange, gson, error, epic, epics);
        } else {
            sendGetResponse(exchange, gson, error, epic, allSubtasksForEpic);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String jsonEpicStr = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        Epic epic = gson.fromJson(jsonEpicStr, Epic.class);
        Map<String,String> error = Map.of();
        Map<String,String> message = Map.of();
        if (epic.getId() == null) {
            try {
                taskManager.addNewEpic(epic);
            } catch (ManagerSaveException exc) {
                error = Map.of("Error_500", exc.getMessage());
            }
            message = Map.of("Message", "Эпик успешно добавлен.");
        } else {
            error = Map.of("Error_400", "Эпик с таким id уже существует!");
        }
        sendPostResponse(exchange, gson, error, message);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicId = getTaskId(exchange);
        Map<String,String> error = Map.of();
        Map<String,String> message = Map.of();
        if (epicId.isEmpty()) {
            error = Map.of("Error_400", "Не указан id удаляемого эпика!");
        } else if (epicId.get() == -1) {
            error = Map.of("Error_400", "Некорректный идентификатор задачи!");
        } else {
            try {
                taskManager.removeEpic(epicId.get());
            } catch (ManagerSaveException exc) {
                error = Map.of("Error_500", exc.getMessage());
            }
            message = Map.of("Message", "Эпик успешно удален.");
        }
        sendDeleteResponse(exchange, gson, error, message);
    }
}

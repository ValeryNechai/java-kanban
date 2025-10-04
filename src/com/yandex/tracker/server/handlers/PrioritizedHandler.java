package com.yandex.tracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetPrioritized(exchange);
                break;
            default:
                writeResponse(exchange, gson, "Такого эндпоинта не существует.", 404);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        List<Task> tasksPrioritized = taskManager.getPrioritizedTasks();
        writeResponse(exchange, gson, tasksPrioritized, 200);
    }
}

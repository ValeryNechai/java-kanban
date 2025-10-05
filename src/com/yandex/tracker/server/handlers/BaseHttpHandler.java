package com.yandex.tracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public abstract class BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected void writeResponse(HttpExchange exchange, Gson gson, Object object, int responseCode) throws IOException {
        String jsonResponse = gson.toJson(object);
        byte[] jsonResponseBytes = jsonResponse.getBytes(DEFAULT_CHARSET);

        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, jsonResponseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponseBytes);
        }
    }

    protected void sendGetResponse(HttpExchange exchange, Gson gson, Map<String, String> error, Object task,
                                   Object tasks) throws IOException {
        if (!error.isEmpty() && error.containsKey("Error_400")) {
            writeResponse(exchange, gson, error, 400);
        } else if (!error.isEmpty() && error.containsKey("Error_404")) {
            writeResponse(exchange, gson, error, 404);
        } else if (task != null) {
            writeResponse(exchange, gson, task, 200);
        } else {
            writeResponse(exchange, gson, tasks, 200);
        }
    }

    protected void sendPostResponse(HttpExchange exchange, Gson gson, Map<String, String> error,
                                    Map<String,String> message) throws IOException {
        if (!error.isEmpty() && error.containsKey("Error_406")) {
            writeResponse(exchange, gson, error, 406);
        } else if (!error.isEmpty() && error.containsKey("Error_500")) {
            writeResponse(exchange, gson, error, 500);
        } else if (!error.isEmpty() && error.containsKey("Error_400")) {
            writeResponse(exchange, gson, error, 400);
        } else {
            writeResponse(exchange, gson, message, 201);
        }
    }

    protected void sendDeleteResponse(HttpExchange exchange, Gson gson, Map<String, String> error,
                                      Map<String, String> message) throws IOException {
        if (!error.isEmpty() && error.containsKey("Error_500")) {
            writeResponse(exchange, gson, error, 500);
        } else if (!error.isEmpty() && error.containsKey("Error_400")) {
            writeResponse(exchange, gson, error, 400);
        } else {
            writeResponse(exchange, gson, message, 200);
        }
    }

    protected Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] partsPath = exchange.getRequestURI().getPath().split("/");
        if (partsPath.length >= 3 && !partsPath[2].isEmpty()) {
            try {
                return Optional.of(Integer.parseInt(partsPath[2]));
            } catch (NumberFormatException e) {
                return Optional.of(-1);
            }
        }
        return Optional.empty();
    }
}

package com.yandex.tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {
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

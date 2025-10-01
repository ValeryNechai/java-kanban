package com.yandex.tracker;

import com.yandex.tracker.server.HttpTaskServer;
import com.yandex.tracker.service.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        String home = System.getProperty("user.home");
        Path testFile = Paths.get(home, "file.txt");
        if (!Files.exists(testFile)) {
            Files.createFile(testFile);
        } else {
            System.out.println("Файл " + testFile.getFileName() + " уже существует.");
        }

        TaskManager taskManager = Managers.getDefault(testFile.toFile());
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);

        taskServer.start(8080);
    }
}


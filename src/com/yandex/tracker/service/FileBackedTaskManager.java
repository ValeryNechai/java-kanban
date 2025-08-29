package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File fileBackedTaskManager;

    public FileBackedTaskManager(File fileBackedTaskManager) {
        this.fileBackedTaskManager = fileBackedTaskManager;
        loadFile(fileBackedTaskManager);
    }

    public static void main(String[] args) throws IOException {
        String home = System.getProperty("user.home");
        Path testFile = Paths.get(home, "file.txt");
        if (!Files.exists(testFile)) {
            Files.createFile(testFile);
        } else {
            System.out.println("Файл " + testFile.getFileName() + " уже существует.");
        }
        FileBackedTaskManager fileBackedTaskManager = loadFromFile(testFile.toFile());
        Task task11 = new Task("Прогуляться", "сквер возле дома", TaskStatus.NEW);
        Task task12 = new Task("Сходить в магазин", "купить овощи", TaskStatus.NEW);
        final int taskID11 = fileBackedTaskManager.addNewTask(task11);
        final int taskID12 = fileBackedTaskManager.addNewTask(task12);

        Epic epic10 = new Epic("Ремонт", "Обновить цвет стен");
        Epic epic11 = new Epic("Подготовка отчета", "Организовать сбор информации");
        final int epicID10 = fileBackedTaskManager.addNewEpic(epic10);
        final int epicID11 = fileBackedTaskManager.addNewEpic(epic11);

        Subtask subtask10 = new Subtask("Купить краску", "Бежевого цвета", TaskStatus.NEW,
                epicID10);
        Subtask subtask11 = new Subtask("Купить кисть", "Большую", TaskStatus.NEW,
                epicID10);
        Subtask subtask12 = new Subtask("Покрасить", "Аккуратно", TaskStatus.NEW,
                epicID10);
        fileBackedTaskManager.addNewSubtask(subtask10);
        fileBackedTaskManager.addNewSubtask(subtask11);
        fileBackedTaskManager.addNewSubtask(subtask12);


        System.out.println("Задачи успешно созданы и сохранены!");
        System.out.println("Файл: " + testFile);
        System.out.println(fileBackedTaskManager.getAllEpics());

    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileBackedTaskManager), StandardCharsets.UTF_8))) {
            bw.write("id,type,name,status,description,epic");
            bw.newLine();

            List<Task> allTasks = new ArrayList<>();
            allTasks.addAll(getAllTasks());
            allTasks.addAll(getAllSubtasks());
            allTasks.addAll(getAllEpics());

            allTasks.sort(Comparator.comparingInt(Task::getId));

            for (Task task : allTasks) {
                bw.write(task.toString());
                bw.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл " + fileBackedTaskManager.getName());
        }
    }

    public void loadFile(File file) {
        try (BufferedReader br = new BufferedReader((new InputStreamReader(new FileInputStream(file),
                StandardCharsets.UTF_8)))) {

            br.readLine(); //Пропускаю заголовок
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Task task = fromString(line.trim());
                if (task != null) {
                    if (task instanceof Subtask) {
                        addNewSubtask((Subtask) task);
                    } else if (task instanceof Epic) {
                        addNewEpic((Epic) task);
                    } else {
                        addNewTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла " + file.getName());
        }
    }

    public String toString(Task task) { //сохранение задачи в строку
        return task.toString();
    }

    public Task fromString(String value) { //создание задачи из строки
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String[] taskFromString = value.split(",");
        int id = Integer.parseInt(taskFromString[0].trim());
        String name = taskFromString[2];
        String description = taskFromString[4];
        TaskStatus status;
        if (taskFromString[3].trim().equals("NEW")) {
            status = TaskStatus.NEW;
        } else if (taskFromString[3].trim().equals("DONE")) {
            status = TaskStatus.DONE;
        } else {
            status = TaskStatus.IN_PROGRESS;
        }
        if (taskFromString[1].trim().equals("TASK")) {
            Task task = new Task(id, name, description, status);
            return task;
        } else if (taskFromString[1].trim().equals("SUBTASK")) {
            int epicId = Integer.parseInt(taskFromString[5].trim());
            Task subtask = new Subtask(id, name, description, status, epicId);
            return subtask;
        } else if (taskFromString[1].trim().equals("EPIC")) {
            Task epic = new Epic(id, name, description, status);
            return epic;
        }
        return null;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksForEpic(Epic epic) {
        return super.getAllSubtasksForEpic(epic);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateEpicStatus(Integer id) {
        super.updateEpicStatus(id);
        save();
    }

    @Override
    public void removeTask(Integer id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeSubtask(Integer id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeEpic(Integer id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }
    
    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
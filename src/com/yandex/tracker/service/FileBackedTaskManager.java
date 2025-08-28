package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File fileBackedTaskManager;

    public FileBackedTaskManager(File fileBackedTaskManager) {
        this.fileBackedTaskManager = fileBackedTaskManager;
        loadFile(fileBackedTaskManager);
    }

    public static void main(String[] args) throws IOException {
        String HOME = System.getProperty("user.home");
        Path testFile = Paths.get(HOME, "file.txt");
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
        final int subtaskID10 = fileBackedTaskManager.addNewSubtask(subtask10);
        final int subtaskID11 = fileBackedTaskManager.addNewSubtask(subtask11);
        final int subtaskID12 = fileBackedTaskManager.addNewSubtask(subtask12);

        System.out.println("Задачи успешно созданы и сохранены!");
        System.out.println("Файл: " + testFile);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBacked = new FileBackedTaskManager(file);
        return fileBacked;
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileBackedTaskManager))) {
            bw.write("id;type;name;status;description,epic/idSubtask");
            bw.newLine();

            for (Task val : getAllTasks()) {
                bw.write(val.toString());
                bw.newLine();;
            }

            for (Subtask val : getAllSubtasks()) {
                bw.write(val.toString());
                bw.newLine();;
            }

            for (Epic val : getAllEpics()) {
                bw.write(val.toString());
                bw.newLine();;
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл " + fileBackedTaskManager.getName());
        }
    }

    public void loadFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                Task task = fromString(line);
                if (task instanceof Task) {
                    addNewTask(task);
                } else if (task instanceof Epic) {
                    addNewEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    addNewSubtask((Subtask) task);
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
        String[] taskFromString = value.split(";");
        int id = Integer.parseInt(taskFromString[0].trim());
        String name = taskFromString[2];
        String description = taskFromString[4];
        TaskStatus status;
        if (taskFromString[3].equals("NEW")) {
            status = TaskStatus.NEW;
        } else if (taskFromString[3].equals("DONE")) {
            status = TaskStatus.DONE;
        } else {
            status = TaskStatus.IN_PROGRESS;
        }
        if (taskFromString[1].equals("TASK")) {
            Task task = new Task(id, name, description, status);
            return task;
        } else if (taskFromString[1].equals("SUBTASK")) {
            int epicId = Integer.parseInt(taskFromString[5].trim());
            Task subtask = new Subtask(id, name, description, status, epicId);
            return subtask;
        } else {
            String idSub = taskFromString[5];
            String newIdSub = idSub.substring(1, idSub.length()-1);
            ArrayList<Integer> idSubtask = new ArrayList<>();
            if (!newIdSub.isBlank()) {
                String[] allIdSub = newIdSub.split(", ");
                for (String idS : allIdSub) {
                    idSubtask.add(Integer.parseInt(idS.trim()));
                }
            }
            Task epic = new Epic(id, name, description, idSubtask);
            return epic;
        }
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
        ArrayList<Task> allTasks = super.getAllTasks();
        return allTasks;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = super.getAllSubtasks();
        return allSubtasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = super.getAllEpics();
        return allEpics;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksForEpic(Epic epic) {
        ArrayList<Subtask> allSubtasksForEpic = super.getAllSubtasksForEpic(epic);
        return allSubtasksForEpic;
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
        List<Task> history = super.getHistory();
        return history;
    }
}
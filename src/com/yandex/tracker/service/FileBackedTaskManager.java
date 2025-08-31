package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File fileBackedTaskManager;
    private static final String heading = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File fileBackedTaskManager) {
        this.fileBackedTaskManager = fileBackedTaskManager;
        loadFile(fileBackedTaskManager);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileBackedTaskManager), StandardCharsets.UTF_8))) {
            bw.write(heading);
            bw.newLine();

            List<Task> allTasks = new ArrayList<>();
            allTasks.addAll(getAllEpics());
            allTasks.addAll(getAllSubtasks());
            allTasks.addAll(getAllTasks());

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
                    if (task.getTaskType() == TaskType.SUBTASK) {
                        addNewSubtaskWithoutSaving((Subtask) task);
                    } else if (task.getTaskType() == TaskType.EPIC) {
                        addNewEpicWithoutSaving((Epic) task);
                    } else {
                        addNewTaskWithoutSaving(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла " + file.getName());
        }
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

    private int addNewTaskWithoutSaving(Task task) {
        return super.addNewTask(task);
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    private int addNewEpicWithoutSaving(Epic epic) {
        return super.addNewEpic(epic);
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    private int addNewSubtaskWithoutSaving(Subtask subtask) {
        return super.addNewSubtask(subtask);
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
}
package com.yandex.tracker.service;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Set<Task>  tasksByPriority = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int addNewTask(Task task) {
        int id = IDGenerator.getID();
        task.setId(id);
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            tasksByPriority.add(task);
        }
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = IDGenerator.getID();
        epic.setId(id);
        epics.put(id, epic);
        if (epic.getStartTime() != null) {
            tasksByPriority.add(epic);
        }
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = IDGenerator.getID();

        if (subtask == null) {
            throw new IllegalArgumentException("Подзадача не может быть null.");
        }

        Epic epic = epics.get(subtask.getEpicID());

        if (epic == null) {
            throw new IllegalArgumentException("Эпик не найден.");
        }

        subtask.setId(id);
        if (subtask.getId() == epic.getId()) {
            throw new IllegalArgumentException("Нельзя добавить подзадачу в саму себя.");
        }

        subtasks.put(id, subtask);
        if (subtask.getStartTime() != null) {
            tasksByPriority.add(subtask);
        }
        Epic epicForSubtask = epics.get(subtask.getEpicID());
        ArrayList<Integer> subtaskForEpic = epicForSubtask.getIdSubtasks();
        subtaskForEpic.add(id);
        updateEpicStatus(subtask.getEpicID());
        updateEpicTimeAndDuration(subtask.getEpicID());
        return id;
    }

    @Override
    public Task getTask(int id) {
        Optional<Task> task = Optional.ofNullable(tasks.get(id));
        if (task.isPresent()) {
            historyManager.add(task.get());
            return task.get();
        } else {
            throw new NullPointerException("Ошибка: передан неинициализированный объект!");
        }
    }

    @Override
    public Epic getEpic(int id) {
        Optional<Epic> epic = Optional.ofNullable(epics.get(id));
        if (epic.isPresent()) {
            historyManager.add(epic.get());
            return epic.get();
        } else {
            throw new NullPointerException("Ошибка: передан неинициализированный объект!");
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        Optional<Subtask> subtask = Optional.ofNullable(subtasks.get(id));
        if (subtask.isPresent()) {
            historyManager.add(subtask.get());
            return subtask.get();
        } else {
            throw new NullPointerException("Ошибка: передан неинициализированный объект!");
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = (ArrayList<Task>) tasks.values().stream()
                .map(task -> new Task(task.getId(), task.getName(),
                task.getDescription(), task.getStatus(), task.getStartTime(), task.getDuration()))
                .collect(Collectors.toList());
        return allTasks;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = (ArrayList<Subtask>) subtasks.values().stream()
                .map(subtask -> new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                        subtask.getStatus(), subtask.getStartTime(), subtask.getDuration(), subtask.getEpicID()))
                .collect(Collectors.toList());
        return allSubtasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = (ArrayList<Epic>) epics.values().stream()
                .map(epic -> new Epic(epic.getId(), epic.getName(), epic.getDescription(), epic.getIdSubtasks(),
                    epic.getStatus(), epic.getStartTime(), epic.getDuration()))
                .collect(Collectors.toList());
        return allEpics;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(tasksByPriority);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        Optional<Task> taskToUpdate = tasksByPriority.stream().filter(t -> t.getId() == task.getId()).findFirst();
        taskToUpdate.ifPresent(oldTask -> {
            tasksByPriority.remove(oldTask);
            tasksByPriority.add(task);
        });
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicID());
        updateEpicTimeAndDuration((subtask.getEpicID()));
        Optional<Task> subtaskToUpdate = tasksByPriority.stream().filter(t -> t.getId() == subtask.getId()).findFirst();
        tasksByPriority.remove(subtaskToUpdate.get());
        tasksByPriority.add(subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        Optional<Task> epicToUpdate = tasksByPriority.stream().filter(t -> t.getId() == epic.getId()).findFirst();
        tasksByPriority.remove(epicToUpdate.get());
        tasksByPriority.add(epic);
    }

    @Override
    public void updateEpicStatus(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
            if (idSubtasks.isEmpty()) {
                epic.setStatus(TaskStatus.NEW);
                return;
            }
            int statusNew = 0;
            int statusDone = 0;
            int statusAll = 0;
            for (Integer subtask : idSubtasks) {
                Subtask subtask1 = subtasks.get(subtask);
                if (subtask1.getStatus().equals(TaskStatus.NEW)) {
                    statusNew++;
                    statusAll++;
                } else if (subtask1.getStatus().equals(TaskStatus.DONE)) {
                    statusDone++;
                    statusAll++;
                } else if (subtask1.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                    statusAll++;
                }
            }
            if (statusAll == statusDone) {
                epic.setStatus(TaskStatus.DONE);
            } else if (statusAll == statusNew) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    public void updateEpicTimeAndDuration(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
            if (idSubtasks.isEmpty()) {
                return;
            }
            List<Subtask> allSubtasksForEpic = idSubtasks.stream().map(subtask -> subtasks.get(subtask))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            if (allSubtasksForEpic.isEmpty()) {
                epic.setStartTime(null);
                epic.setEndTime(null);
                epic.setDuration(Duration.ofMinutes(0));
                return;
            }
            Optional<Subtask> startSubtask = allSubtasksForEpic.stream()
                    .min(Comparator.comparing(Task::getStartTime));

            startSubtask.ifPresent(subtask -> epic.setStartTime(subtask.getStartTime()));

            Optional<Subtask> endSubtask = allSubtasksForEpic.stream()
                    .max(Comparator.comparing(subtask -> subtask.getEndTime()));

            endSubtask.ifPresent(subtask -> epic.setEndTime(subtask.getEndTime()));

            Duration epicDuration = allSubtasksForEpic.stream().map(Subtask::getDuration)
                            .filter(Objects::nonNull).reduce(Duration.ofMinutes(0), Duration::plus);

            epic.setDuration(epicDuration);
        }
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksForEpic(Epic epic) {
        ArrayList<Subtask> allSubtasksForEpic = epic.getIdSubtasks().stream().map(subtasks::get)
                .collect(Collectors.toCollection(ArrayList::new));
        return allSubtasksForEpic;
    }

    @Override
    public void removeTask(Integer id) {
        tasks.remove(id);
        historyManager.remove(id);
        Optional<Task> taskToRemove = tasksByPriority.stream().filter(t -> t.getId() == id).findFirst();
        tasksByPriority.remove(taskToRemove.get());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        for (Task task : tasks.values()) {
            int id = task.getId();
            historyManager.remove(id);
            Optional<Task> taskToRemove = tasksByPriority.stream().filter(t -> t.getId() == id).findFirst();
            tasksByPriority.remove(taskToRemove.get());
        }
    }

    @Override
    public void removeSubtask(Integer id) {
        Subtask removedValue = subtasks.remove(id);
        if (removedValue != null) {
            Epic epic = epics.get(removedValue.getEpicID());
            ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
            idSubtasks.remove(id);
            updateEpicStatus(removedValue.getEpicID());
        }
        historyManager.remove(id);
        Optional<Task> subtaskToRemove = tasksByPriority.stream().filter(t -> t.getId() == id).findFirst();
        tasksByPriority.remove(subtaskToRemove.get());
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            ArrayList<Integer> idSubtasks = epic.getIdSubtasks();
            idSubtasks.clear();
            epic.setStatus(TaskStatus.NEW);
        }
        for (Subtask subtask : subtasks.values()) {
            int id = subtask.getId();
            historyManager.remove(id);
            Optional<Task> subtaskToRemove = tasksByPriority.stream().filter(t -> t.getId() == id).findFirst();
            tasksByPriority.remove(subtaskToRemove.get());
        }
    }

    @Override
    public void removeEpic(Integer id) {
        Epic removedValue = epics.remove(id);
        if (removedValue != null) {
            ArrayList<Integer> idSubtasksForEpic = removedValue.getIdSubtasks();
            for (Integer idSubtasks : idSubtasksForEpic) {
                subtasks.remove(idSubtasks);
                historyManager.remove(idSubtasks);
                Optional<Task> epicToRemove = tasksByPriority.stream().filter(t -> t.getId() == id).findFirst();
                epicToRemove.ifPresent(tasksByPriority::remove);
            }
        }
        historyManager.remove(id);
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
        for (Epic epic : epics.values()) {
            int id = epic.getId();
            historyManager.remove(id);
            Optional<Task> epicToRemove = tasksByPriority.stream().filter(t -> t.getId() == id).findFirst();
            tasksByPriority.remove(epicToRemove.get());
        }
    }

    @Override
    public boolean checkingTaskTimesCross() {
        List<Task> prioritizedTasks = getPrioritizedTasks();
        try {
            Optional<Boolean> check = prioritizedTasks.stream().filter(task -> task.getStartTime() != null
                            && task.getEndTime() != null).filter(task -> !(task.getClass() == Epic.class))
                    .sorted(Comparator.comparing(Task::getStartTime))
                    .reduce((task1, task2) -> {
                        if (task1.getEndTime().isAfter(task2.getStartTime())
                                || task1.getStartTime().equals(task2.getStartTime())) {
                            throw new ManagerCheckingException("Задачи " + task1.getName() + " и " + task2.getName()
                                    + " пересекаются!");
                        }
                        return task2;
                    })
                    .map(task -> false);
            return false; //пересечений нет
        } catch (RuntimeException exc) {
            return true; //найдено пересечение
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}





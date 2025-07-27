package com.yandex.tracker;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.service.InMemoryTaskManager;
import com.yandex.tracker.service.Managers;
import com.yandex.tracker.service.TaskManager;
import com.yandex.tracker.service.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        //Создание task, subtask, epic
        Task task1 = new Task("Прогуляться", "сквер возле дома", TaskStatus.NEW);
        Task task2 = new Task("Сходить в магазин", "купить овощи", TaskStatus.NEW);
        final int taskID1 = taskManager.addNewTask(task1);
        final int taskID2 = taskManager.addNewTask(task2);

        Epic epic1 = new Epic("Ремонт", "Обновить цвет стен");
        Epic epic2 = new Epic("Подготовка отчета", "Организовать сбор информации");
        final int epicID1 = taskManager.addNewEpic(epic1);
        final int epicID2 = taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Купить краску", "Бежевого цвета", TaskStatus.NEW,
                epicID1);
        Subtask subtask2 = new Subtask("Покрасить стены", "Однотонный вариант", TaskStatus.NEW,
                epicID1);
        Subtask subtask3 = new Subtask("Обзвонить службы", "ООПД и ОКС", TaskStatus.NEW, epicID2);
        final int subtaskID1 = taskManager.addNewSubtask(subtask1);
        final int subtaskID2 = taskManager.addNewSubtask(subtask2);
        final int subtaskID3 = taskManager.addNewSubtask(subtask3);

        //Получение списка всех задач
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("____________________________");

        //Получение и обновление данных
        Task task3 = taskManager.getTask(taskID1);
        task3.setStatus(TaskStatus.DONE);
        task3.setDescription("Нет, все-таки поедем в лес");
        taskManager.updateTask(task3);

        Subtask subtask4 = taskManager.getSubtask(subtaskID2);
        subtask4.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask4);

        Epic epic3 = taskManager.getEpic(epicID1);
        taskManager.updateEpic(epic3);

        //Получение списка всех задач (проверка изменений)
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("____________________________");

        //Получение списка всех подзадач эпика
        System.out.println("Все подзадачи входящие в эпик с названием: " + epic1.getName());
        for (Subtask epicsSubtasks : taskManager.getAllSubtasksForEpic(epic1)) {
            System.out.println("Подзадача: " + epicsSubtasks);
        }
        System.out.println("____________________________");

        printAllTasks(taskManager);


        //Удаление по ID
        taskManager.removeTask(taskID1);
        taskManager.removeSubtask(6);
        taskManager.removeEpic(2);

        //Проверка выборочного удаления задач
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("____________________________");

        //Удаление всех задач
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();

        //Проверка удаления всех задач
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("____________________________");

        System.out.println(taskManager.getHistory());
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtaskForEpic : manager.getAllSubtasksForEpic(epic)) {
                System.out.println("--> " + subtaskForEpic);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}


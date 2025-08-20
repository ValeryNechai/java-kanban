package com.yandex.tracker;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.service.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        //Спринт 6. Реализую пользовательский сценарий

        Task task11 = new Task("Прогуляться", "сквер возле дома", TaskStatus.NEW);
        Task task12 = new Task("Сходить в магазин", "купить овощи", TaskStatus.NEW);
        final int taskID11 = taskManager.addNewTask(task11);
        final int taskID12 = taskManager.addNewTask(task12);

        Epic epic10 = new Epic("Ремонт", "Обновить цвет стен");
        Epic epic11 = new Epic("Подготовка отчета", "Организовать сбор информации");
        final int epicID10 = taskManager.addNewEpic(epic10);
        final int epicID11 = taskManager.addNewEpic(epic11);

        Subtask subtask10 = new Subtask("Купить краску", "Бежевого цвета", TaskStatus.NEW,
                epicID10);
        Subtask subtask11 = new Subtask("Купить кисть", "Большую", TaskStatus.NEW,
                epicID10);
        Subtask subtask12 = new Subtask("Покрасить", "Аккуратно", TaskStatus.NEW,
                epicID10);
        final int subtaskID10 = taskManager.addNewSubtask(subtask10);
        final int subtaskID11 = taskManager.addNewSubtask(subtask11);
        final int subtaskID12 = taskManager.addNewSubtask(subtask12);

        taskManager.getTask(taskID11);
        taskManager.getEpic(epicID11);
        taskManager.getEpic(epicID10);
        taskManager.getSubtask(subtaskID10);
        taskManager.getSubtask(subtaskID11);
        System.out.println(taskManager.getHistory());
        taskManager.getTask(taskID11);
        System.out.println(taskManager.getHistory());
        taskManager.removeEpic(epicID11);
        System.out.println(taskManager.getHistory());
        taskManager.removeEpic(epicID10);
        System.out.println(taskManager.getHistory());


/*
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
        System.out.println("____________________________");



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

        printAllTasks(taskManager);
*/
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task subtaskForEpic : manager.getAllSubtasksForEpic((Epic) epic)) {
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


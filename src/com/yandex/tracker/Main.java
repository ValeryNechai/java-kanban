package com.yandex.tracker;

import com.yandex.tracker.model.Epic;
import com.yandex.tracker.model.Subtask;
import com.yandex.tracker.model.Task;
import com.yandex.tracker.service.TaskManager;
import com.yandex.tracker.service.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        //Создание task, subtask, epic
        Task task1 = new Task("Прогуляться", "сквер возле дома", TaskStatus.NEW);
        Task task2 = new Task("Сходить в магазин", "купить овощи", TaskStatus.NEW);
        final int taskIDNumber1 = taskManager.addNewTask(task1);
        final int taskIDNumber2 = taskManager.addNewTask(task2);

        Epic epic1 = new Epic("Ремонт", "Обновить цвет стен");
        Epic epic2 = new Epic("Подготовка отчета", "Организовать сбор информации");
        final int epicIDNumber1 = taskManager.addNewEpic(epic1);
        final int epicIDNumber2 = taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Купить краску", "Бежевого цвета", TaskStatus.NEW,
                epicIDNumber1);
        Subtask subtask2 = new Subtask("Покрасить стены", "Однотонный вариант", TaskStatus.NEW,
                epicIDNumber1);
        Subtask subtask3 = new Subtask("Обзвонить службы", "ООПД и ОКС", TaskStatus.NEW, epicIDNumber2);
        final int subtaskIDNumber1 = taskManager.addNewSubtask(subtask1);
        final int subtaskIDNumber2 = taskManager.addNewSubtask(subtask2);
        final int subtaskIDNumber3 = taskManager.addNewSubtask(subtask3);

        //Получение списка всех задач
        taskManager.printAllTasks();
        taskManager.printAllSubtasks();
        taskManager.printAllEpics();
        System.out.println("____________________________");

        //Получение и обновление данных
        Task task3 = taskManager.getTask(taskIDNumber1);
        task3.setStatus(TaskStatus.DONE);
        task3.setDescription("Нет, все-таки поедем в лес");
        taskManager.updateTask(task3);

        Subtask subtask4 = taskManager.getSubtask(subtaskIDNumber2);
        subtask4.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask4);

        Epic epic3 = taskManager.getEpic(epicIDNumber1);
        taskManager.updateEpicStatus(epicIDNumber1);
        taskManager.updateEpic(epic3);

        //Получение списка всех задач (проверка изменений)
        taskManager.printAllTasks();
        taskManager.printAllSubtasks();
        taskManager.printAllEpics();
        System.out.println("____________________________");

        //Получение списка всех подзадач эпика
        System.out.println("Все подзадачи входящие в эпик с названием: " + epic1.getName());
        for (Subtask epicsSubtasks : taskManager.getAllSubtasksForEpic(epic1)) {
            System.out.println("Подзадача: " + epicsSubtasks);
        }
        System.out.println("____________________________");

        //Удаление по ID
        taskManager.removeTask(taskIDNumber1);
        taskManager.removeSubtask(6);
        taskManager.removeEpic(2);

        //Проверка выборочного удаления задач
        taskManager.printAllTasks();
        taskManager.printAllSubtasks();
        taskManager.printAllEpics();
        System.out.println("____________________________");

        //Удаление всех задач
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();

        //Проверка удаления всех задач
        taskManager.printAllTasks();
        taskManager.printAllSubtasks();
        taskManager.printAllEpics();
        System.out.println("____________________________");
    }
}


package com.yandex.tracker.service;

import com.yandex.tracker.model.Node;
import com.yandex.tracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodeHistory = new HashMap<>();

    private Node head;
    private Node tail;
    private int size = 0;

    public void linkLast(Task task) { //добавление задачи в конец списка
        Node t = tail;
        Node newNode = new Node(task, t, null);
        tail = newNode;
        if (t == null) {
            head = newNode;
        } else {
            t.setNext(newNode);
        }
        size++;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        Node value = head;
        while (value != null) {
            taskList.add(value.getTask());
            value = value.getNext();
        }
        return taskList;
    }

    public void removeNode(int id) {
        Node removeNode = nodeHistory.remove(id);
        if (removeNode == null) {
            return;
        }
        if (head == tail) {
            head = null;
            tail = null;
        } else if (removeNode == head) {
            head = removeNode.getNext();
            head.setPrev(null);
        } else if (removeNode == tail) {
            tail = removeNode.getPrev();
            tail.setNext(null);
        } else {
            removeNode.getPrev().setNext(removeNode.getNext());
            removeNode.getNext().setPrev(removeNode.getPrev());
        }
    }

    @Override
    public void add(Task task) {
        int id = task.getId();
        if (task != null) {
            if (nodeHistory.containsKey(id)) {
                removeNode(id);
            }
            linkLast(task);
            nodeHistory.put(id, tail);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    public Map<Integer, Node> getNodeHistory() {
        return nodeHistory;
    }
}

package com.yandex.tracker.service;

import com.yandex.tracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    public static class Node {
        private Task task;
        private Node next;
        private Node prev;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        public Node(Task task) {
            this.task = task;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "task=" + task +
                    ", next=" + (next == null ? null : next.task) +
                    ", prev=" + (prev == null ? null : prev.task) +
                    '}';
        }
    }

    private final Map<Integer, Node> nodeHistory = new HashMap<>();

    private final Node dummyHead;
    private final Node dummyTail;

    public InMemoryHistoryManager() {
        dummyHead = new Node(null);
        dummyTail = new Node(null);
        dummyHead.next = dummyTail;
        dummyTail.prev = dummyHead;
    }
    
    private int size = 0;

    private void linkLast(Task task) { //добавление задачи в конец списка
        Node t = dummyTail.prev;
        Node newNode = new Node(task, t, dummyTail);
        t.next = newNode;
        dummyTail.prev = newNode;
        size++;
    }

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node value = dummyHead.next;
        while (value != dummyTail) {
            taskList.add(value.task);
            value = value.next;
        }
        return taskList;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    @Override
    public void add(Task task) {
        int id = task.getId();
        if (task != null) {
            remove(id);
            linkLast(task);
            nodeHistory.put(id, dummyHead.prev);
        }
    }

    @Override
    public void remove(int id) {
        Node removeNodes = nodeHistory.remove(id);
        if (removeNodes != null) {
            removeNode(removeNodes);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public Node getHead() {
        return dummyHead;
    }

    public Node getTail() {
        return dummyTail;
    }
}

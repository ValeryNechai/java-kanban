package com.yandex.tracker.model;

public class Node {
    private Task task;
    private Node next;
    private Node prev;

    public Node(Task task, Node prev, Node next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }

    @Override
    public String toString() {
        return "Node{" +
                "task=" + task +
                ", next=" + (next == null ? null : next.task) +
                ", prev=" + (prev == null ? null : prev.task) +
                '}';
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}

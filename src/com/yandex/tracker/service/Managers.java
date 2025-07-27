package com.yandex.tracker.service;

public final class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}

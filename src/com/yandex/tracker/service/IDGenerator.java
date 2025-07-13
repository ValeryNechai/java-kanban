package com.yandex.tracker.service;

public class IDGenerator {
    private static int counter = 0;

    public static int getIDNumber() {
        return counter++;
    }
}

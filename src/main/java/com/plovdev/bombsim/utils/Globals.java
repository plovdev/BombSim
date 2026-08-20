package com.plovdev.bombsim.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Globals {
    public static final ExecutorService VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private static final List<Runnable> SHUTDOWN_HOOKS = new CopyOnWriteArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(Globals.class);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(VIRTUAL_EXECUTOR::close));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> SHUTDOWN_HOOKS.forEach(Runnable::run)));
    }

    private Globals() {
        throw new UnsupportedOperationException();
    }

    public static void addShutdownHook(Runnable hook) {
        SHUTDOWN_HOOKS.add(hook);
    }

    public static void removeShutdownHook(Runnable hook) {
        SHUTDOWN_HOOKS.remove(hook);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Sleeping error: ", e);
            Thread.currentThread().interrupt();
        }
    }
}
package com.plovdev.bombsim.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
    private static EventManager instanse = null;
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();
    private final Logger log = LoggerFactory.getLogger(EventManager.class);

    public static EventManager getInstance() {
        if (instanse == null) instanse = new EventManager();
        return instanse;
    }

    private EventManager() {}

    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }
    public void unsubscribe(EventListener listener) {
        listeners.remove(listener);
    }
    public void broadcast(Channel channel) {
        for (EventListener listener : listeners) {
            if (listener.getChanelType() == channel.getType()) {
                try {
                    listener.onEvent(channel);
                } catch (Exception e) {
                    log.error("Execution error: ", e);
                }
            }
        }
    }
}
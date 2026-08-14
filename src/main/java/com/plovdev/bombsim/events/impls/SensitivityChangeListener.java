package com.plovdev.bombsim.events.impls;

import com.plovdev.bombsim.events.Channel;
import com.plovdev.bombsim.events.ChannelType;
import com.plovdev.bombsim.events.Event;
import com.plovdev.bombsim.events.EventListener;

public class SensitivityChangeListener implements EventListener {
    private Event event;
    public SensitivityChangeListener(Event e) {
        event = e;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public void onEvent(Channel channel) {
        event.onEvet(channel.getData());
    }

    @Override
    public ChannelType getChanelType() {
        return null;
    }
}

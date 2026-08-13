package com.plovdev.bombsim.events.impls;

import com.plovdev.bombsim.events.Channel;
import com.plovdev.bombsim.events.ChannelType;
import com.plovdev.bombsim.events.Event;
import com.plovdev.bombsim.events.EventListener;

public class ModelChangeEventListener implements EventListener {
    private Event event;
    public ModelChangeEventListener(Event e) {
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
        return ChannelType.MODEL_CHANGE;
    }
}

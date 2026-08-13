package com.plovdev.bombsim.events.impls;

import com.plovdev.bombsim.events.Channel;
import com.plovdev.bombsim.events.ChannelType;
import com.plovdev.bombsim.events.Event;
import com.plovdev.bombsim.events.EventListener;

public class BombTextChangedEventListener implements EventListener {
    private Event event;
    public BombTextChangedEventListener(Event e) {
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
        return ChannelType.BOMB_TEXT_CHANGE;
    }
}
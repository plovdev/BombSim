package com.plovdev.bombsim.events;

public interface EventListener {
    void onEvent(Channel channel);
    ChannelType getChanelType();
}
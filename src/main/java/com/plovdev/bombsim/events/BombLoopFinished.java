package com.plovdev.bombsim.events;

import org.plovdev.eda.ChannelEvent;

public class BombLoopFinished extends ChannelEvent {
    public BombLoopFinished() {
        super(GlobalEventManager.BOMB_LOOP_FINISHED);
    }
}
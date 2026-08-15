package com.plovdev.bombsim.events;

import org.plovdev.eda.ChannelEvent;

public class BombModelChangeEvent extends ChannelEvent {
    private String newModel;

    public BombModelChangeEvent(String newModel) {
        super(GlobalEventManager.BOMB_MODEL_CHANGE_EVENT);
        this.newModel = newModel;
    }

    public String getNewModel() {
        return newModel;
    }

    public void setNewModel(String newModel) {
        this.newModel = newModel;
    }
}
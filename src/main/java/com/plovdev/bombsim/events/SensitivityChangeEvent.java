package com.plovdev.bombsim.events;

import org.plovdev.eda.ChannelEvent;

public class SensitivityChangeEvent extends ChannelEvent {
    public enum SensitvityType {
        ZOOM, ROTATE
    }

    private SensitvityType sensType;
    private float value;

    public SensitivityChangeEvent(SensitvityType sensType, float value) {
        super(GlobalEventManager.SENSITIVITY_CHANGE_EVENT);
        this.sensType = sensType;
        this.value = value;
    }

    public SensitvityType getSensType() {
        return sensType;
    }

    public void setSensType(SensitvityType sensType) {
        this.sensType = sensType;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
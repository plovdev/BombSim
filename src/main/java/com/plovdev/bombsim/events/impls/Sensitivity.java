package com.plovdev.bombsim.events.impls;

public class Sensitivity {
    public final static int ZOOM = 1;
    public final static int ROTATE = 0;

    private int type;
    private float sensitivity;

    public Sensitivity(int type) {
        this.type = type;
    }

    public Sensitivity(int type, float sens) {
        this.type = type;
        this.sensitivity = sens;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }
}
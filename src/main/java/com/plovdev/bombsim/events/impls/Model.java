package com.plovdev.bombsim.events.impls;

public class Model {
    private String path;

    public Model() {
    }

    public Model(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
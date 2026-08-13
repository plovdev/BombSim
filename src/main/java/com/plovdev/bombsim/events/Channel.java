package com.plovdev.bombsim.events;

public class Channel {
    private ChannelType type;
    private Object data;

    public Channel(ChannelType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }
}
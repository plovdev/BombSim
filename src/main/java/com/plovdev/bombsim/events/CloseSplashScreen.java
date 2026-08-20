package com.plovdev.bombsim.events;

import org.plovdev.eda.ChannelEvent;

public class CloseSplashScreen extends ChannelEvent {
    public CloseSplashScreen() {
        super(GlobalEventManager.CLOSE_SPLASH_SCREEN);
    }
}
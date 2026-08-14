package com.plovdev.bombsim.controls.gui;

public class MainScreenControl extends BaseScreenController {
    @Override
    public void onStartScreen() {
        putScreen(screen.getScreenId());
    }

    @Override
    public void onEndScreen() {
    }
}
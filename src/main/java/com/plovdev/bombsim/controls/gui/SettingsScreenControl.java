package com.plovdev.bombsim.controls.gui;

public class SettingsScreenControl extends BaseScreenController {
    public void back() {
        nifty.gotoScreen(getLastScreen());
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }
}
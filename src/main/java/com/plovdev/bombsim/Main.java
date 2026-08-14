package com.plovdev.bombsim;

import com.jme3.system.AppSettings;
import com.plovdev.bombsim.utils.PreferencesStorage;

import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) {
        LogManager.getLogManager().reset();

        AppSettings settings = PreferencesStorage.getSettings();
        BombSim3D bombSim3D = new BombSim3D(settings);
        bombSim3D.start();
    }
}
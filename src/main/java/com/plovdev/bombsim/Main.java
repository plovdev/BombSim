package com.plovdev.bombsim;

import com.jme3.system.AppSettings;
import com.plovdev.bombsim.utils.PreferencesStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        AppSettings settings = PreferencesStorage.getSettings();
        BombSim3D bombSim3D = new BombSim3D(settings);
        bombSim3D.start();
    }
}
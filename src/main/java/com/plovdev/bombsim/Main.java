package com.plovdev.bombsim;

import com.jme3.system.AppSettings;
import com.plovdev.bombsim.utils.PreferencesStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.LogManager;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        log.info("Starting BombSim");

        startEngine();
    }

    private static void startEngine() {
        AppSettings settings = PreferencesStorage.getSettings();
        BombSim3D bombSim3D = new BombSim3D(settings);
        bombSim3D.start();
    }
}
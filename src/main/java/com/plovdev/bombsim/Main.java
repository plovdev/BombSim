package com.plovdev.bombsim;

import com.jme3.system.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        try {
            settings.load("BombSim");
            System.setProperty("org.lwjgl.openal.dopperFactor", "0.0");
        } catch (Exception e) {
            log.error("Settings loading error: ", e);
            settings.setTitle("BombSim 3");
            settings.setGammaCorrection(true);
            settings.setVSync(true);
            settings.setSamples(2);
            settings.setResizable(true);
            settings.setWindowSize(700,720);
        }

        BombSim3D bombSim3D = new BombSim3D(settings);
        bombSim3D.start();
    }
}
package com.plovdev.bombsim.utils;

import com.jme3.system.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PreferencesStorage {
    private static final AppSettings SETTINGS = new AppSettings(true);
    private static final Logger log = LoggerFactory.getLogger(PreferencesStorage.class);

    static {
        try {
            System.setProperty("org.lwjgl.openal.dopperFactor", "0.0");
            SETTINGS.load("BombSim");
        } catch (Exception e) {
            log.error("Settings loading error: ", e);
            SETTINGS.setTitle("BombSim 3");
            SETTINGS.setGammaCorrection(true);
            SETTINGS.setVSync(true);
            SETTINGS.setSamples(2);
            SETTINGS.setResizable(true);
            SETTINGS.setWindowSize(700, 720);
            SETTINGS.setRenderer(AppSettings.LWJGL_OPENGL41);
        }
    }

    public static final String ROTATE_SENSENSITIVITY = "rotate_sens";
    public static final String ZOOM_SENSENSITIVITY = "zoom_sens";

    private PreferencesStorage() {
    }

    public static AppSettings getSettings() {
        return SETTINGS;
    }

    public static float getFloat(String key, float def) {
        return SETTINGS.getFloat(key, def);
    }

    public static String get(String key, String def) {
        return SETTINGS.getString(key, def);
    }
}
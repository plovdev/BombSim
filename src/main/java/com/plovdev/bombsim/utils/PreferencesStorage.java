package com.plovdev.bombsim.utils;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import com.plovdev.bombsim.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.prefs.BackingStoreException;

public final class PreferencesStorage {
    private static final Logger log = LoggerFactory.getLogger(PreferencesStorage.class);

    private static final AppSettings SETTINGS = new AppSettings(true);
    private static final String PREFS_KEY = "BombSim";
    public static final String ROTATE_SENSENSITIVITY = "rotate_sens";
    public static final String ZOOM_SENSENSITIVITY = "zoom_sens";

    static {
        try {
            System.setProperty("org.lwjgl.openal.dopperFactor", "0.0");
            if (!SETTINGS.getBoolean("was-init", false)) {
                SETTINGS.putBoolean("was-init", true);
                loadDefault();
            } else {
                SETTINGS.load(PREFS_KEY);
            }

            setupIcons();
        } catch (Exception e) {
            log.error("Settings loading error: ", e);
            loadDefault();
        }
    }

    private PreferencesStorage() {
        throw new UnsupportedOperationException();
    }

    public static void savePreferences() {
        try {
            SETTINGS.save(PREFS_KEY);
        } catch (BackingStoreException e) {
            log.error("Error to save preferences: ", e);
        }
    }

    private static void loadDefault() {
        SETTINGS.setTitle("BombSim 3");
        SETTINGS.setGammaCorrection(true);
        SETTINGS.setVSync(true);
        SETTINGS.setSamples(4);
        SETTINGS.setResizable(true);
        SETTINGS.setWindowSize(700, 720);
        SETTINGS.setRenderer(AppSettings.LWJGL_OPENGL41);
    }

    private static void setupIcons() {
        if (JmeSystem.getPlatform().getOs() != Platform.Os.MacOS) {
            try {
                BufferedImage[] icons = new BufferedImage[]{
                        ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/assets/Interface/Icons/bombsim-logo-16.png"))),
                        ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/assets/Interface/Icons/bombsim-logo-32.png"))),
                        ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/assets/Interface/Icons/bombsim-logo-64.png"))),
                        ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/assets/Interface/Icons/bombsim-logo-128.png")))
                };
                SETTINGS.setIcons(icons);
            } catch (Exception e) {
                log.error("Error to setup app icons: ", e);
            }
        }
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
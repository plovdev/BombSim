package com.plovdev.bombsim.commons;

import java.util.prefs.Preferences;

public final class PreferencesStorage {
    private static final Preferences PREFERENCES = Preferences.userRoot().node("BombSim");

    public static final String ROTATE_SENSENSITIVITY = "rotate_sens";
    public static final String ZOOM_SENSENSITIVITY = "zoom_sens";

    private PreferencesStorage() {
    }

    public static float getFloat(String key, float def) {
        return PREFERENCES.getFloat(key, def);
    }

    public static String get(String key, String def) {
        return PREFERENCES.get(key, def);
    }
}
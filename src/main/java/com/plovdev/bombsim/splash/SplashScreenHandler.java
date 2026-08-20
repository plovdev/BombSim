package com.plovdev.bombsim.splash;

import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import com.plovdev.bombsim.events.CloseSplashScreen;
import com.plovdev.bombsim.events.GlobalEventManager;
import org.plovdev.eda.reflect.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class SplashScreenHandler {
    private static final Logger log = LoggerFactory.getLogger(SplashScreenHandler.class);
    private SplashScreen splashScreen;

    public SplashScreenHandler() {
        if (JmeSystem.getPlatform().getOs() != Platform.Os.MacOS) {
            try {
                this.splashScreen = SplashScreen.getSplashScreen();
            } catch (Exception e) {
                log.error("Error get splash screen: ", e);
            }
        }
    }

    public void handleSplashScreen() {
        try {
            if (splashScreen != null) {
                SplashScreenDrawer screenDrawer = new SplashScreenDrawer(splashScreen);
                screenDrawer.startDrawing();
            }
        } catch (Exception e) {
            log.error("Error to handle splash screen: ", e);
        }
    }

    @Subscribe(channel = GlobalEventManager.CLOSE_SPLASH_SCREEN)
    private void close(CloseSplashScreen event) {
        try {
            if (splashScreen != null) {
                splashScreen.close();
            }
        } catch (Exception e) {
            log.error("Error to close splash screen: ", e);
        }
    }
}
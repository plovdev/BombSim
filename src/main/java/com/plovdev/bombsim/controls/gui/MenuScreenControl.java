package com.plovdev.bombsim.controls.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.plovdev.bombsim.states.GameAppState;
import com.plovdev.bombsim.states.MenuAppState;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuScreenControl extends BaseScreenController {
    private static final Logger log = LoggerFactory.getLogger(MenuScreenControl.class);
    private final SimpleApplication application;
    private final AppStateManager stateManager;

    public MenuScreenControl(@NonNull SimpleApplication application) {
        this.application = application;
        this.stateManager = application.getStateManager();
    }

    public void newGame() {
        MenuAppState menuState = stateManager.getState(MenuAppState.class);
        GameAppState gameState = stateManager.getState(GameAppState.class);

        menuState.setEnabled(false);
        gameState.setEnabled(true);
    }

    public void showAuthors() {

    }

    public void exitGame() {
        log.info("Exiting from game...");
        application.stop(true);
        System.exit(0);
    }

    @Override
    public void onStartScreen() {
        putScreen(screen.getScreenId());
    }

    @Override
    public void onEndScreen() {
    }
}
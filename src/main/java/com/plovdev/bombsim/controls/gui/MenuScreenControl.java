package com.plovdev.bombsim.controls.gui;

import com.jme3.app.SimpleApplication;
import com.plovdev.bombsim.events.GameStateEvent;
import com.plovdev.bombsim.events.GlobalEventManager;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuScreenControl extends BaseScreenController {
    private static final Logger log = LoggerFactory.getLogger(MenuScreenControl.class);
    private final SimpleApplication application;

    public MenuScreenControl(@NonNull SimpleApplication application) {
        this.application = application;
    }

    public void newGame() {
        GlobalEventManager.broadcastEvent(new GameStateEvent(GameStateEvent.GameState.GAME));
    }

    public void showAuthors() {
        nifty.gotoScreen("authors");
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
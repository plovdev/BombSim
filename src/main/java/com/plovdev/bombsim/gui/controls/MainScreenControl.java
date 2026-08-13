package com.plovdev.bombsim.gui.controls;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.awt.*;
import java.nio.file.Path;

public class MainScreenControl implements ScreenController {
    private static final Logger log = LoggerFactory.getLogger(MainScreenControl.class);
    private Nifty nifty;
    private Screen screen;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    public Nifty getNifty() {
        return nifty;
    }

    public void setNifty(Nifty nifty) {
        this.nifty = nifty;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {

    }
    public void onSettingsClicked() {
        nifty.gotoScreen("settings");
    }
    public void onHelpClicked() {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(Path.of("src/main/resources/assets/Docs/help.html").toUri());
            } else {
                log.warn("Desktop is not supported.");
            }
        } catch (Exception e) {
            log.error("Help initing error: ", e);
        }
    }

    @Override
    public void onEndScreen() {

    }
}

package com.plovdev.bombsim.gui.controls;

import com.plovdev.bombsim.browser.BrowserOpener;
import com.plovdev.bombsim.utils.Globals;
import com.plovdev.bombsim.utils.Utils;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class MainScreenControl implements ScreenController {
    private static final Logger log = LoggerFactory.getLogger(MainScreenControl.class);
    private Nifty nifty;
    private Screen screen;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onSettingsClicked() {
        nifty.gotoScreen("settings");
    }

    public void onHelpClicked() {
        Globals.VIRTUAL_EXECUTOR.execute(() -> Utils.showHelp(BrowserOpener.getInstance()));
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }
}
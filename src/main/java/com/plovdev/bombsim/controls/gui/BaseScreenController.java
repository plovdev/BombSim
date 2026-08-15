package com.plovdev.bombsim.controls.gui;

import com.plovdev.bombsim.browser.BrowserOpener;
import com.plovdev.bombsim.utils.Globals;
import com.plovdev.bombsim.utils.Utils;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.util.Stack;

public abstract class BaseScreenController implements ScreenController {
    private static final Stack<String> screenHistory = new Stack<>();
    protected static final BrowserOpener BROWSER_OPENER = BrowserOpener.getInstance();
    private static final Logger log = LoggerFactory.getLogger(BaseScreenController.class);

    protected Nifty nifty;
    protected Screen screen;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void putScreen(String screenId) {
        screenHistory.push(screenId);
    }

    public String getLastScreen() {
        return screenHistory.pop();
    }

    public void onSettingsClicked() {
        nifty.gotoScreen("settings");
    }

    public void onHelpClicked() {
        Globals.VIRTUAL_EXECUTOR.execute(() -> Utils.showHelp(BROWSER_OPENER));
    }

    public void openLink(String url) {
        Globals.VIRTUAL_EXECUTOR.execute(() -> {
            try {
                BROWSER_OPENER.open(URI.create(url));
            } catch (IOException e) {
                log.error("Error to open link {}: ", url, e);
            }
        });
    }
}
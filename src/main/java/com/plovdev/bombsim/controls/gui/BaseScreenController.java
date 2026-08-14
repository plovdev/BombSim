package com.plovdev.bombsim.controls.gui;

import com.plovdev.bombsim.browser.BrowserOpener;
import com.plovdev.bombsim.utils.Globals;
import com.plovdev.bombsim.utils.Utils;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;
import java.util.Stack;

public abstract class BaseScreenController implements ScreenController {
    private static final Stack<String> screenHistory = new Stack<>();

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
        Globals.VIRTUAL_EXECUTOR.execute(() -> Utils.showHelp(BrowserOpener.getInstance()));
    }
}
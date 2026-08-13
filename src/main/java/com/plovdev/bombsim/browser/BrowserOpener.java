package com.plovdev.bombsim.browser;

import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.URI;

public interface BrowserOpener {
    static @NonNull BrowserOpener getInstance() {
        Platform.Os os = JmeSystem.getPlatform().getOs();
        return switch (os) {
            case MacOS -> new MacBrowserOpener();
            case Windows -> new WindowsBrowserOpener();
            default -> throw new UnsupportedOperationException("Platform not supported");
        };
    }

    void open(URI uri) throws IOException;
}
package com.plovdev.bombsim.browser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class WindowsBrowserOpener implements BrowserOpener {
    private static final Logger log = LoggerFactory.getLogger(WindowsBrowserOpener.class);

    @Override
    public void open(URI uri) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(uri);
        }
    }
}
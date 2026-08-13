package com.plovdev.bombsim.browser;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.URI;

public class MacBrowserOpener implements BrowserOpener {
    @Override
    public void open(@NonNull URI uri) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("open", uri.toString());
        pb.inheritIO();
        pb.start();
    }
}
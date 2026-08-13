package com.plovdev.bombsim.utils;

import com.plovdev.bombsim.Main;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    private static final AtomicReference<URI> HELP_DOC_HOLDER = new AtomicReference<>(null);

    public static void showPasswordErrorDialoge() {
    }

    public static void showHelp() {
        try {
            HELP_DOC_HOLDER.compareAndSet(null, unpackHelpDocument());
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(HELP_DOC_HOLDER.get());
            } else {
                log.warn("Desktop is not supported.");
                //TODO: show dialog
            }
        } catch (Exception e) {
            log.error("Help initing error: ", e);
        }
    }

    private static @NonNull URI unpackHelpDocument() {
        try (InputStream helpDocInputStream = Main.class.getResourceAsStream("/assets/Docs/help.html")) {
            File helpFile = Files.createTempFile("help_document", ".html").toFile();
            helpFile.deleteOnExit();

            try (FileOutputStream os = new FileOutputStream(helpFile)) {
                Objects.requireNonNull(helpDocInputStream).transferTo(os);
            }

            return helpFile.toURI();
        } catch (Exception e) {
            log.error("Error to unpack help document: ", e);
            throw new RuntimeException("Can't unpack help doc: " + e.getMessage());
        }
    }
}
package com.plovdev.bombsim.splash;

import com.plovdev.bombsim.events.GlobalEventManager;
import com.plovdev.bombsim.utils.Globals;
import org.jetbrains.annotations.NotNull;
import org.plovdev.eda.reflect.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class SplashScreenDrawer {
    private static final Random random = new Random();
    private static final Logger log = LoggerFactory.getLogger(SplashScreenDrawer.class);
    private final SwingWorker<Void, Void> animationThread;
    private final SplashScreen screen;
    private final Graphics2D splashGraphics;
    private final Image backgroundImage;

    private final int width;
    private final int height;
    private final int fuseY;
    private final int fuseLength;
    private int startX;
    private final int endX;

    private volatile boolean running = true;

    public SplashScreenDrawer(@NotNull SplashScreen screen) {
        this.screen = screen;
        this.splashGraphics = screen.createGraphics();

        try {
            this.backgroundImage = ImageIO.read(screen.getImageURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Rectangle bounds = screen.getBounds();
        this.width = bounds.width;
        this.height = bounds.height;
        this.startX = width / 3;
        this.endX = width;
        this.fuseY = height * 3 / 4;
        this.fuseLength = width * 9 / 10;

        this.animationThread = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                while (running && !isCancelled() && screen.isVisible()) {
                    drawSplashScreen();
                    Globals.sleep(30);
                }
                return null;
            }
        };

        setupSplashScreen();
    }

    private void setupSplashScreen() {
        splashGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private void drawSplashScreen() {
        if (screen == null) return;
        splashGraphics.drawImage(backgroundImage, 0, 0, null);

        startX += 2;
        if (startX > width) {
            startX = -fuseLength;
        }

        splashGraphics.setColor(new Color(160, 160, 160));
        splashGraphics.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        splashGraphics.drawLine(startX, fuseY, endX, fuseY);

        splashGraphics.setColor(new Color(130, 130, 130));
        for (int x = startX + 10; x < endX - 10; x += 6 + random.nextInt(8)) {
            if (random.nextInt(3) == 0) {
                splashGraphics.fillRect(x, fuseY - 1, 2, 2);
            }
            if (random.nextInt(5) == 0) {
                splashGraphics.drawLine(x, fuseY - 2, x + 3, fuseY + 2);
            }
        }

        splashGraphics.setColor(new Color(100, 100, 100));
        for (int x = startX + 20; x < endX - 20; x += 15 + random.nextInt(20)) {
            splashGraphics.drawLine(x, fuseY - 1, x + 5, fuseY + 1);
        }

        drawSparks(startX);
        screen.update();
    }

    private void drawSparks(int startX) {
        for (int i = 0; i < 40; i++) {
            int x = startX - random.nextInt(-6, 5);
            int y = fuseY - random.nextInt(-4, 6);
            int size = 2;
            int alpha = 150 + random.nextInt(100);

            if (random.nextBoolean()) {
                splashGraphics.setColor(new Color(255, 200, 50, alpha));
            } else {
                splashGraphics.setColor(new Color(255, 100, 0, alpha));
            }

            splashGraphics.fillRect(x, y, size, size);
        }
    }

    public void startDrawing() {
        animationThread.execute();
    }

    @Subscribe(channel = GlobalEventManager.CLOSE_SPLASH_SCREEN)
    private void close() {
        running = false;
        splashGraphics.dispose();
        animationThread.cancel(true);
    }
}
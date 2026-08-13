package com.plovdev.bombsim.controls;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.plovdev.bombsim.audio.AudioManager;
import com.plovdev.bombsim.events.EventManager;
import com.plovdev.bombsim.events.impls.Sensitivity;
import com.plovdev.bombsim.events.impls.SensitivityChangeEventLitener;
import com.plovdev.bombsim.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

public class BombControl extends AbstractControl {
    private static final Logger log = LoggerFactory.getLogger(BombControl.class);
    private final Preferences prefs = Preferences.userRoot().node("BombSim").node("Settings");

    private final AssetManager assetManager;
    private Node bombModel;
    private InputManager inputManager;
    private float distance = 10f;
    private float azm = 0;
    private float elevation = 0f;
    private float lastX, lastY;
    private boolean rotating = false;
    private boolean dragging = false;
    private boolean buttonPressed = false;
    private AudioManager audioManager;
    private BombTextControl textControl;
    private boolean isExposed = false;
    private AudioNode ticksNode = new AudioNode();

    private final Timer executor = new Timer();
    private final Timer deffuseExecutor = new Timer();

    private Camera cam;
    private float sensitivity = prefs.getFloat("rotate-sens", 0.005f);
    private float zoomSensitivity = prefs.getFloat("zoom-sens", 0.25f);

    public BombControl(Node bomb, InputManager im, AssetManager assetManager, Camera c) {
        bombModel = bomb;
        this.assetManager = assetManager;
        audioManager = new AudioManager(assetManager, bombModel);
        textControl = new BombTextControl(bomb, assetManager, im);
        bombModel.addControl(audioManager);
        bombModel.addControl(textControl);
        inputManager = im;
        cam = c;

        EventManager.getInstance().subscribe(new SensitivityChangeEventLitener(e -> {
            if (e instanceof Sensitivity sens) {
                switch (sens.getType()) {
                    case Sensitivity.ROTATE:
                        this.sensitivity = sens.getSensitivity();
                        break;
                    case Sensitivity.ZOOM:
                        this.zoomSensitivity = sens.getSensitivity();
                        break;
                }
            }
        }));

        inputManager.addMapping("Rotate", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Drag", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.addMapping("PressButton", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));

        inputManager.addListener((ActionListener) (s, b, v) -> {
            switch (s) {
                case "Rotate":
                    rotating = b;
                    buttonPressed = b;
                    if (rotating) {
                        lastX = inputManager.getCursorPosition().x;
                        lastY = inputManager.getCursorPosition().y;
                    }
                    break;
                case "Drag":
                    dragging = b;
                    break;
            }
        }, "Rotate", "Drag");
        inputManager.addListener((AnalogListener) (s, b, v) -> {
            if (s.equals("ZoomIn")) {
                distance -= zoomSensitivity;
                updateCamera();
            } else if (s.equals("ZoomOut")) {
                distance += zoomSensitivity;
                updateCamera();
            }
        }, "ZoomIn", "ZoomOut");
        updateCamera();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public BombTextControl getTextControl() {
        return textControl;
    }

    public void setTextControl(BombTextControl textControl) {
        this.textControl = textControl;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public float getZoomSensitivity() {
        return zoomSensitivity;
    }

    public void setZoomSensitivity(float zoomSensitivity) {
        this.zoomSensitivity = zoomSensitivity;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getAzm() {
        return azm;
    }

    public void setAzm(float azm) {
        this.azm = azm;
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public float getLastX() {
        return lastX;
    }

    public void setLastX(float lastX) {
        this.lastX = lastX;
    }

    public float getLastY() {
        return lastY;
    }

    public void setLastY(float lastY) {
        this.lastY = lastY;
    }

    public boolean isRotating() {
        return rotating;
    }

    public void setRotating(boolean rotating) {
        this.rotating = rotating;
    }

    public Camera getCam() {
        return cam;
    }

    public void setCam(Camera cam) {
        this.cam = cam;
    }

    public Preferences getPrefs() {
        return prefs;
    }

    public boolean isButtonPressed() {
        return buttonPressed;
    }

    public void setButtonPressed(boolean buttonPressed) {
        this.buttonPressed = buttonPressed;
    }

    private void updateCamera() {
        float x = distance * FastMath.cos(elevation) * FastMath.sin(azm);
        float y = distance * FastMath.sin(elevation);
        float z = distance * FastMath.cos(elevation) * FastMath.cos(azm);

        cam.setLocation(new Vector3f(x, -y, z));
        cam.lookAt(bombModel.getLocalTranslation(), Vector3f.UNIT_Y);
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    public Spatial getBombModel() {
        return bombModel;
    }

    public void setBombModel(Node bombModel) {
        this.bombModel = bombModel;
    }

    public boolean isExposed() {
        return isExposed;
    }

    public void setExposed(boolean exposed) {
        isExposed = exposed;
    }

    public Timer getExecutor() {
        return executor;
    }

    @Override
    protected void controlUpdate(float v) {
        if (bombModel == null || inputManager == null || cam == null) return;

        if (buttonPressed) {
            buttonPressed = false;
            try {
                CollisionResults results = new CollisionResults();
                Vector2f click = inputManager.getCursorPosition();
                Vector3f clickPosition = cam.getWorldCoordinates(new Vector2f(click.x, click.y), 15f);
                Vector3f clickDirection = cam.getWorldCoordinates(new Vector2f(click.x, click.y), 100).subtractLocal(clickPosition);
                Ray clickRay = new Ray(clickPosition, clickDirection);
                bombModel.getParent().collideWith(clickRay, results);
                if (results.size() > 0) {
                    Geometry clicked = results.getClosestCollision().getGeometry();
                    String name = clicked.getName().replaceAll("_\\d+", "");
                    if (name.matches("Button([\\d+]|[#|*])")) {
                        handleClick(bombModel.getChild(name + "Node"));
                    }
                }
            } catch (Exception e) {
                log.error("Click handling error: ", e);
            }
        }

        if (rotating) {
            float x = inputManager.getCursorPosition().x;
            float y = inputManager.getCursorPosition().y;

            float dx = x - lastX;
            float dy = y - lastY;

            azm -= dx * sensitivity;
            elevation += dy * sensitivity;
            elevation = FastMath.clamp(elevation, -FastMath.HALF_PI + 0.1f, FastMath.HALF_PI - 0.1f);

            updateCamera();
            lastX = x;
            lastY = y;
        }
        //TODO:
//        if (dragging) {
//            float x = inputManager.getCursorPosition().x;
//            float y = inputManager.getCursorPosition().y;
//            Vector3f pos = bombModel.getLocalTranslation();
//            bombModel.move(new Vector3f(x, y, pos.z));
//        }
    }

    private void handleClick(Spatial clicked) {
        String num = clicked.getUserData("Number");
        AnimComposer composer = clicked.getControl(AnimComposer.class);
        composer.setCurrentAction("Press" + num, AnimComposer.DEFAULT_LAYER, false);
        String inputedText = textControl.getChars();
        notifyTextUpdate(num);
        audioManager.playButtonPress(clicked);
        if (num.equals("*") || num.equals("#")) {
            String passw = prefs.get("password", "774774");
            textControl.clear();
            if (inputedText.equals(passw)) {
                if (num.equals("*")) {
                    plant();
                } else {
                    diffuse();
                }
            } else {
                log.warn("Error password: {}, of {}", inputedText, passw);
                Utils.showPasswordErrorDialoge();
            }
        }
    }

    private void plant() {
        ticksNode = audioManager.playTicks(bombModel);
        executor.schedule(new TimerTask() {
            @Override
            public void run() {
                explode();
            }
        }, 40000);
    }

    private void diffuse() {
        deffuseExecutor.schedule(new TimerTask() {
            @Override
            public void run() {
                executor.cancel();
                try {
                    ticksNode.stop();
                    ticksNode.removeFromParent();
                } catch (Exception e) {
                    log.error("Stop ticks error: {}", e.getMessage());
                }
            }
        }, 5000);
    }

    public void explode() {
        audioManager.playExplode(bombModel);
        createParticle("Fire");
        createParticle("Spark");
        createParticle("Smoke");
        createParticle("Debris");
        createParticle("Shokwave");
    }

    private void createParticle(String name) {

    }

    private void notifyTextUpdate(String text) {
        textControl.updateController(text);
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

    }
}
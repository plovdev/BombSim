package com.plovdev.bombsim.controls;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
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
import com.plovdev.bombsim.commons.PreferencesStorage;
import com.plovdev.bombsim.events.EventManager;
import com.plovdev.bombsim.events.impls.Sensitivity;
import com.plovdev.bombsim.events.impls.SensitivityChangeEventLitener;
import com.plovdev.bombsim.utils.Utils;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BombControl extends AbstractControl {
    private static final Logger log = LoggerFactory.getLogger(BombControl.class);

    private final AssetManager assetManager;
    private Node bombModel;
    private final InputManager inputManager;
    private float distance = 10f;
    private float azm = 0;
    private float elevation = 0f;
    private float lastX, lastY;
    private boolean rotating = false;
    private boolean dragging = false;
    private boolean buttonPressed = false;
    private final AudioManager audioManager;
    private final BombTextControl textControl;

    private final Camera cam;
    private float rotateSensitivity = PreferencesStorage.getFloat(PreferencesStorage.ROTATE_SENSENSITIVITY, 0.005f);
    private float zoomSensitivity = PreferencesStorage.getFloat(PreferencesStorage.ZOOM_SENSENSITIVITY, 0.25f);

    public BombControl(InputManager im, AssetManager assetManager, Camera c) {
        this.assetManager = assetManager;
        this.audioManager = new AudioManager(assetManager);
        this.textControl = new BombTextControl(assetManager, im);
        this.inputManager = im;
        this.cam = c;

        EventManager.getInstance().subscribe(new SensitivityChangeEventLitener(e -> {
            if (e instanceof Sensitivity sens) {
                switch (sens.getType()) {
                    case Sensitivity.ROTATE:
                        this.rotateSensitivity = sens.getSensitivity();
                        break;
                    case Sensitivity.ZOOM:
                        this.zoomSensitivity = sens.getSensitivity();
                        break;
                }
            }
        }));

        inputManager.addMapping("Rotate", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Drag", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
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
                updateDistance(distance - zoomSensitivity);
                updateCamera();
            } else if (s.equals("ZoomOut")) {
                updateDistance(distance + zoomSensitivity);
                updateCamera();
            }
        }, "ZoomIn", "ZoomOut");
    }

    private void updateDistance(float newDistane) {
        distance = Math.max(2, Math.min(newDistane, 20));
    }

    @Override
    public void setSpatial(Spatial spatial) {
        bombModel = (Node) spatial;
        bombModel.addControl(textControl);
        updateCamera();
    }

    private void updateCamera() {
        float offsetXZ = distance * FastMath.cos(elevation);
        float camX = offsetXZ * FastMath.sin(azm);
        float camY = distance * FastMath.sin(elevation);
        float camZ = offsetXZ * FastMath.cos(azm);

        Vector3f bombPos = bombModel.getLocalTranslation();
        cam.setLocation(new Vector3f(bombPos.x + camX, bombPos.y - camY, bombPos.z + camZ));

        cam.lookAt(bombPos, Vector3f.UNIT_Y);
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

            azm -= dx * rotateSensitivity;
            elevation += dy * rotateSensitivity;
            elevation = FastMath.clamp(elevation, -FastMath.HALF_PI + 0.1f, FastMath.HALF_PI - 0.1f);

            updateCamera();
            lastX = x;
            lastY = y;
        }
    }

    private void handleClick(@NonNull Spatial clicked) {
        String num = clicked.getUserData("Number");
        AnimComposer composer = clicked.getControl(AnimComposer.class);
        composer.setCurrentAction("Press" + num, AnimComposer.DEFAULT_LAYER, false);

        String inputedText = textControl.getChars();
        notifyTextUpdate(num);
        audioManager.playButtonPress(clicked);

        if (num.equals("*") || num.equals("#")) {
            String password = PreferencesStorage.get("password", "7355608");
            textControl.clear();
            if (inputedText.equals(password)) {
                if (num.equals("*")) {
                    //TODO: plant bomb
                } else {
                    //TODO: diffuse bomb
                }
            } else {
                log.warn("Error password {}", inputedText);
                Utils.showPasswordErrorDialoge();
            }
        }
    }

    private void notifyTextUpdate(String text) {
        textControl.updateController(text);
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
    }
}
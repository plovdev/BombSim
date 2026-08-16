package com.plovdev.bombsim.controls;

import com.jme3.anim.AnimComposer;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
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
import com.plovdev.bombsim.events.GlobalEventManager;
import com.plovdev.bombsim.events.SensitivityChangeEvent;
import com.plovdev.bombsim.states.BombPlantedAppState;
import com.plovdev.bombsim.utils.PreferencesStorage;
import org.jspecify.annotations.NonNull;
import org.plovdev.eda.reflect.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BombControl extends AbstractControl {
    private static final Logger log = LoggerFactory.getLogger(BombControl.class);

    private Node bombModel;
    private float distance = 10f;
    private float rotationX = 0;
    private float rotationY = 0;
    private float lastX, lastY;
    private boolean rotating = false;
    private boolean dragging = false;
    private boolean buttonPressed = false;
    private String bombText;
    private Quaternion bombRotation;

    private final AssetManager assetManager;
    private final InputManager inputManager;
    private final AudioManager audioManager;
    private final AppStateManager stateManager;

    private final BombTextControl textControl;
    private final Camera cam;
    private final BombPlantedAppState plantedAppState;

    private float rotateSensitivity = PreferencesStorage.getFloat(PreferencesStorage.ROTATE_SENSITIVITY, 0.005f);
    private float zoomSensitivity = PreferencesStorage.getFloat(PreferencesStorage.ZOOM_SENSITIVITY, 0.25f);

    private final CollisionResults results = new CollisionResults();
    private final Ray clickRay = new Ray();
    private final Vector3f clickPosition = new Vector3f();
    private final Vector3f clickDirection = new Vector3f();

    public BombControl(@NonNull Application application) {
        this.assetManager = application.getAssetManager();
        this.inputManager = application.getInputManager();
        this.stateManager = application.getStateManager();
        this.cam = application.getCamera();

        this.textControl = new BombTextControl(assetManager, inputManager);
        this.plantedAppState = stateManager.getState(BombPlantedAppState.class);
        this.audioManager = stateManager.getState(AudioManager.class);

        inputManager.addMapping("Rotate", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Drag", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("PressButton", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        inputManager.addListener((ActionListener) (s, b, v) -> {
            if (!isEnabled()) return;
            switch (s) {
                case "Rotate":
                    rotating = b;
                    buttonPressed = b;
                    if (rotating) {
                        Vector2f cursor = inputManager.getCursorPosition();
                        lastX = cursor.x;
                        lastY = cursor.y;
                    }
                    break;
                case "Drag":
                    dragging = b;
                    break;
            }
        }, "Rotate", "Drag");

        inputManager.addListener((AnalogListener) (s, b, v) -> {
            if (!isEnabled()) return;
            if (s.equals("ZoomIn")) {
                updateDistance(distance - zoomSensitivity);
                updateCamera();
            } else if (s.equals("ZoomOut")) {
                updateDistance(distance + zoomSensitivity);
                updateCamera();
            }
        }, "ZoomIn", "ZoomOut");
    }

    private void updateDistance(float newDistance) {
        distance = Math.max(2, Math.min(newDistance, 20));
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null) {
            bombModel = (Node) spatial;
            bombModel.addControl(textControl);
            bombText = "";
            bombRotation = new Quaternion().fromAngles(0, 0, 0);
            bombRotation.set(Quaternion.IDENTITY);
            rotationX = 0;
            rotationY = 0;
            updateCamera();
        }
    }

    private void updateCamera() {
        if (bombModel == null) return;

        Vector3f bombPos = bombModel.getLocalTranslation();
        Vector3f camPos = new Vector3f(0, 0, distance);
        cam.setLocation(camPos);
        cam.lookAt(bombPos, Vector3f.UNIT_Y);
    }

    @Subscribe(channel = GlobalEventManager.SENSITIVITY_CHANGE_EVENT)
    private void updatSensitivity(@NonNull SensitivityChangeEvent event) {
        float newValue = event.getValue();
        switch (event.getSensType()) {
            case ROTATE -> this.rotateSensitivity = newValue;
            case ZOOM -> this.zoomSensitivity = newValue;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            textControl.updateController(bombText);
            bombModel.setLocalRotation(bombRotation);
            updateCamera();
        } else {
            bombText = textControl.getChars();
            textControl.clear();
            if (bombModel != null) {
                bombRotation.set(bombModel.getLocalRotation());
                bombModel.setLocalRotation(Quaternion.IDENTITY);
            }
        }
    }

    @Override
    protected void controlUpdate(float v) {
        if (!isEnabled() || bombModel == null || inputManager == null || cam == null) return;

        if (buttonPressed) {
            buttonPressed = false;
            try {
                results.clear();
                Vector2f click = inputManager.getCursorPosition();

                cam.getWorldCoordinates(click, 0f, clickPosition);
                cam.getWorldCoordinates(click, 1f, clickDirection);
                clickDirection.subtractLocal(clickPosition).normalizeLocal();

                clickRay.setOrigin(clickPosition);
                clickRay.setDirection(clickDirection);

                bombModel.getParent().collideWith(clickRay, results);
                if (results.size() > 0) {
                    Geometry clicked = results.getClosestCollision().getGeometry();

                    String name = clicked.getName();
                    if (name.startsWith("Button")) {
                        String cleanName = name.split("_")[0];
                        Spatial buttonNode = bombModel.getChild(cleanName + "Node");
                        if (buttonNode != null) {
                            handleClick(buttonNode);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error handling click event: ", e);
            }
        }

        if (rotating) {
            Vector2f cursor = inputManager.getCursorPosition();
            float x = cursor.x;
            float y = cursor.y;

            float dx = x - lastX;
            float dy = y - lastY;

            rotationY += dx * rotateSensitivity;
            rotationX += dy * rotateSensitivity;

            bombRotation.fromAngles(-rotationX, rotationY, 0);
            bombModel.setLocalRotation(bombRotation);

            lastX = x;
            lastY = y;
        }
    }

    private void handleClick(@NonNull Spatial clicked) {
        String num = clicked.getUserData("Number");
        if (num == null) return;

        AnimComposer composer = clicked.getControl(AnimComposer.class);
        if (composer != null) {
            composer.setCurrentAction("Press" + num, AnimComposer.DEFAULT_LAYER, false);
        }

        String inputText = textControl.getChars();
        textControl.updateController(num);
        audioManager.playButtonClick(clicked);

        if (num.equals("*") || num.equals("#")) {
            String password = PreferencesStorage.get("password", "7355608");
            textControl.clear();
            if (inputText.equals(password)) {
                switchState(num.equals("#"));
            } else {
                log.warn("Incorrect password entered: {}", inputText);
            }
        }
    }

    private void switchState(boolean plant) {
        if (plant) {
            plantedAppState.plant();
        } else {
            plantedAppState.dDefuse();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
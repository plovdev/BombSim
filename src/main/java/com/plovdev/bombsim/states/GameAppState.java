package com.plovdev.bombsim.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.plovdev.bombsim.controls.BombControl;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameAppState extends BaseAppState {
    private static final Logger log = LoggerFactory.getLogger(GameAppState.class);

    private InputManager inputManager;
    private AssetManager assetManager;
    private Camera camera;
    private BombControl bombControl;

    private final Node bombNode;

    public GameAppState(Node bombNode) {
        this.bombNode = bombNode;
    }

    @Override
    protected void initialize(@NonNull Application app) {
        SimpleApplication application = (SimpleApplication) app;

        this.inputManager = application.getInputManager();
        this.assetManager = application.getAssetManager();
        this.camera = application.getCamera();

        this.bombControl = new BombControl(inputManager, assetManager, camera);
    }

    @Override
    protected void cleanup(Application application) {
        if (bombControl != null && bombNode != null) {
            bombNode.removeControl(bombControl);
            bombControl = null;
        }
    }

    @Override
    protected void onEnable() {
        if (bombControl != null && bombNode != null) {
            if (bombNode.getControl(BombControl.class) == null) {
                bombNode.addControl(bombControl);
                log.info("BombControl attached to the bomb");
            } else {
                log.warn("BombControl already has been attached");
            }
        }
    }

    @Override
    protected void onDisable() {
        if (bombControl != null && bombNode != null) {
            bombNode.removeControl(bombControl);
            log.info("BombNode detacched from the bomb");
        }
    }
}
package com.plovdev.bombsim.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.plovdev.bombsim.controls.BombControl;
import de.lessvoid.nifty.Nifty;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameAppState extends BaseAppState {
    private static final Logger log = LoggerFactory.getLogger(GameAppState.class);

    private BombControl bombControl;
    private final Node bombNode;
    private final Nifty nifty;

    public GameAppState(Node bombNode, Nifty nifty) {
        this.bombNode = bombNode;
        this.nifty = nifty;
    }

    @Override
    protected void initialize(@NonNull Application app) {
        SimpleApplication application = (SimpleApplication) app;

        InputManager inputManager = application.getInputManager();
        AssetManager assetManager = application.getAssetManager();
        Camera camera = application.getCamera();

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
                nifty.gotoScreen("main");
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
            nifty.gotoScreen("menu");
            log.info("BombControl removed from the bomb");
        }
    }
}
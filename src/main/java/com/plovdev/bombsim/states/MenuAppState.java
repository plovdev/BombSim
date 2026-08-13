package com.plovdev.bombsim.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import org.jspecify.annotations.NonNull;

public class MenuAppState extends BaseAppState {
    private InputManager inputManager;
    private AssetManager assetManager;
    private Camera camera;

    private final Node bombNode;

    public MenuAppState(Node bombNode) {
        this.bombNode = bombNode;
    }

    @Override
    protected void initialize(@NonNull Application app) {
        SimpleApplication application = (SimpleApplication) app;

        this.inputManager = application.getInputManager();
        this.assetManager = application.getAssetManager();
        this.camera = application.getCamera();
    }

    @Override
    protected void cleanup(Application application) {
    }

    @Override
    protected void onEnable() {
        //TODO: manage gui
    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void update(float tpf) {
        if (isEnabled()) {

        }
    }
}
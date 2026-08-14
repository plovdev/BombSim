package com.plovdev.bombsim.states;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import org.jspecify.annotations.NonNull;

public class MenuAppState extends BaseAppState {
    private static final float ORBIT_RADIUS = 13.5f;
    private static final float ORBIT_SPEED = 0.1F;
    private static final float HEIGHT_OFFSET = 0.5f;

    private Application application;
    private Camera camera;
    private float angle = 0;

    private final Node bombNode;
    private final Nifty nifty;

    public MenuAppState(Node bombNode, Nifty nifty) {
        this.bombNode = bombNode;
        this.nifty = nifty;
    }

    @Override
    protected void initialize(@NonNull Application app) {
        this.application = app;
        this.camera = app.getCamera();
        updateCameraPosition(0);
    }

    @Override
    protected void cleanup(Application application) {
    }

    @Override
    protected void onEnable() {
        nifty.gotoScreen("menu");
        application.enqueue(() -> bombNode.setLocalRotation(new Quaternion().fromAngles(0, 0, 0)));
    }

    @Override
    protected void onDisable() {
        updateCameraPosition(0);
    }

    @Override
    public void update(float tpf) {
        if (isEnabled()) {
            angle += tpf * ORBIT_SPEED;
            updateCameraPosition(angle);
        }
    }

    private void updateCameraPosition(float angle) {
        float x = (float) (ORBIT_RADIUS * Math.sin(angle));
        float z = (float) (ORBIT_RADIUS * Math.cos(angle));

        Vector3f camPos = new Vector3f(x, HEIGHT_OFFSET, z);
        camera.setLocation(camPos);
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
}
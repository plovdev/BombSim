package com.plovdev.bombsim.controls;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.jspecify.annotations.NonNull;

public class BombTimerControl extends AbstractControl {
    private final BitmapText timerText;
    private Spatial display;
    private final Node timerDisplayNode = new Node("DisplayNode");
    private Node bombModel;

    public BombTimerControl(@NonNull AssetManager assetManager) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        font.setRightToLeft(false);
        timerText = new BitmapText(font);
        timerText.setSize(0.25f);
        timerText.setQueueBucket(RenderQueue.Bucket.Transparent);
        timerText.setColor(ColorRGBA.Black);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        bombModel = (Node) spatial;
        bombModel.attachChild(timerDisplayNode);
        display = bombModel.getChild("Display");

        timerDisplayNode.attachChild(timerText);
        timerDisplayNode.setLocalTranslation(calculateTextPosition());
    }

    private @NonNull Vector3f calculateTextPosition() {
        Vector3f textPos = display.getLocalTranslation();
        float x = textPos.x + 0.5f;
        float y = textPos.y + 0.945f;
        float z = textPos.z + 0.724f;
        return new Vector3f(x, y, z);
    }

    public void updateTimer(int time) {
        timerText.setText(String.valueOf(time));
    }

    public void reset() {
        timerText.setText("");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            timerDisplayNode.attachChild(timerText);
        } else {
            timerDisplayNode.detachChild(timerText);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
    }
}
package com.plovdev.bombsim.audio;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class AudioManager extends AbstractControl {
    private AssetManager assetManager;
    private Spatial model;
    private Node parent;

    public AudioManager(AssetManager am, Spatial spatial) {
        assetManager = am;
        model = spatial;
        parent = spatial.getParent();
        playBackground();
    }

    public Node getParent() {

        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Spatial getModel() {
        return model;
    }

    public void setModel(Spatial model) {
        this.model = model;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public AudioNode play(Vector3f pos, Vector3f dir, String path, AudioData.DataType type, boolean loop) {
        AudioNode audioNode = new AudioNode(assetManager, path, type);
        audioNode.setLooping(loop);
        if (pos == null) {
            audioNode.setPositional(false);
        } else {
            audioNode.setPositional(true);
            audioNode.setLocalTranslation(pos);
            audioNode.setRefDistance(5);
            audioNode.setMaxDistance(20);
        }
        if (dir == null) {
            audioNode.setDirectional(false);
        } else {
            audioNode.setDirectional(true);
            audioNode.setDirection(dir);
        }
        parent.attachChild(audioNode);
        audioNode.play();
        return audioNode;
    }

    public void playBackground() {
        play(null, null, "assets/Sounds/bomb-playback.wav", AudioData.DataType.Stream, true).setVolume(0.2f);
    }

    public void playButtonPress(Spatial button) {
        play(button.getLocalTranslation(), null, "assets/Sounds/button-pressed.wav", AudioData.DataType.Buffer, false);
    }

    public AudioNode playTicks(Node bomb) {
        return play(bomb.getLocalTranslation(), null, "assets/Sounds/bomb-ticks.wav", AudioData.DataType.Stream, false);
    }

    public void playExplode(Node bomb) {
        play(bomb.getLocalTranslation(), null, "assets/Sounds/explode.wav", AudioData.DataType.Buffer, false);
    }

    @Override
    protected void controlUpdate(float v) {

    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

    }
}
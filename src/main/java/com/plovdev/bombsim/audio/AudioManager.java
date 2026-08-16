package com.plovdev.bombsim.audio;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.jspecify.annotations.NonNull;

import static com.plovdev.bombsim.audio.AudioPlayerUtils.play;

public class AudioManager extends BaseAppState {
    private AssetManager assetManager;

    public void playPlant(Node root) {
        if (assetManager == null) throw new IllegalStateException("AssetManager is null");
        play(assetManager, root, null, null, "assets/Sounds/plant.wav", AudioData.DataType.Buffer, false, 1);
    }

    public void playTick(@NonNull Node bomb) {
        if (assetManager == null) throw new IllegalStateException("AssetManager is null");
        play(assetManager, bomb, bomb.getLocalTranslation(), null, "assets/Sounds/bomb-tick.wav", AudioData.DataType.Buffer, false, 1);
    }

    public void playButtonClick(@NonNull Spatial button) {
        if (assetManager == null) throw new IllegalStateException("AssetManager is null");
        play(assetManager, (Node) button, button.getLocalTranslation(), null, "assets/Sounds/button-pressed.wav", AudioData.DataType.Buffer, false, 1);
    }

    public void playExplode(@NonNull Node bomb) {
        if (assetManager == null) throw new IllegalStateException("AssetManager is null");
        play(assetManager, bomb, bomb.getLocalTranslation(), null, "assets/Sounds/explode.wav", AudioData.DataType.Buffer, false, 10);
    }

    @Override
    protected void initialize(@NonNull Application application) {
        this.assetManager = application.getAssetManager();
    }

    @Override
    protected void cleanup(Application application) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
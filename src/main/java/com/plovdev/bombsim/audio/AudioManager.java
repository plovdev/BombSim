package com.plovdev.bombsim.audio;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.jspecify.annotations.NonNull;

import static com.plovdev.bombsim.audio.AudioPlayerUtils.play;

public class AudioManager {
    private final AssetManager assetManager;

    public AudioManager(AssetManager am) {
        assetManager = am;
    }

    public void playButtonPress(@NonNull Spatial button) {
        play(assetManager, button.getParent(), button.getLocalTranslation(), null, "assets/Sounds/button-pressed.wav", AudioData.DataType.Buffer, false);
    }

    public @NonNull AudioNode playTicks(@NonNull Node bomb) {
        return play(assetManager, bomb, bomb.getLocalTranslation(), null, "assets/Sounds/bomb-ticks.wav", AudioData.DataType.Stream, false);
    }

    public void playExplode(@NonNull Node bomb) {
        play(assetManager, bomb, bomb.getLocalTranslation(), null, "assets/Sounds/explode.wav", AudioData.DataType.Buffer, false);
    }
}
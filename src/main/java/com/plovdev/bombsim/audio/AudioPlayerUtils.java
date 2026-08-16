package com.plovdev.bombsim.audio;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.jspecify.annotations.NonNull;

public final class AudioPlayerUtils {
    public static @NonNull AudioNode play(AssetManager assetManager, Node parent, Vector3f pos, Vector3f dir, String path, AudioData.DataType type, boolean loop, float vol) {
        AudioNode audioNode = new AudioNode(assetManager, path, type);
        audioNode.setLooping(loop);
        audioNode.setVolume(vol);
        if (pos == null) {
            audioNode.setPositional(false);
        } else {
            audioNode.setPositional(true);
            audioNode.setLocalTranslation(pos);
            audioNode.setRefDistance(5);
            audioNode.setMaxDistance(20);
            audioNode.setVelocity(Vector3f.ZERO);
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
}
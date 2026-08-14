package com.plovdev.bombsim;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.plovdev.bombsim.audio.AudioPlayerUtils;
import org.jspecify.annotations.NonNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BombSimInitializer {
    private BombSimInitializer() {
    }

    public static void init(@NonNull SimpleApplication application, FilterPostProcessor fpp) {
        Node root = application.getRootNode();
        AssetManager assetManager = application.getAssetManager();

        addLight(root);
        applyFilters(fpp, root);
        initSky(assetManager, root);

        application.getFlyByCamera().setEnabled(false);
        application.setDisplayFps(false);
        application.setDisplayStatView(false);

        createSparkEmitter(new Vector3f(0f, 0f, 0f), root, assetManager, "BombSpark1");
        createSparkEmitter(new Vector3f(6.5f, 7.5f, 0f), root, assetManager, "BombSpark2");
        createSparkEmitter(new Vector3f(-6.5f, -7.5f, 0f), root, assetManager, "BombSpark3");
        createSparkEmitter(new Vector3f(6.5f, -7.5f, 0f), root, assetManager, "BombSpark4");
        createSparkEmitter(new Vector3f(-6.5f, 7.5f, 0f), root, assetManager, "BombSpark4");

        AudioPlayerUtils.play(assetManager, root, null, null, "assets/Sounds/bomb-playback.wav", AudioData.DataType.Stream, true).setVolume(0.2f);
    }

    private static void addLight(@NonNull Node root) {
        root.addLight(new AmbientLight(ColorRGBA.White));
        root.addLight(new DirectionalLight(new Vector3f(0, 1, 0), ColorRGBA.White));
        root.addLight(new DirectionalLight(new Vector3f(-10, 1, -10), ColorRGBA.White));
        root.addLight(new DirectionalLight(new Vector3f(10, 1f, 10), ColorRGBA.White));
    }

    public static void applyFilters(FilterPostProcessor fpp, Node root) {
//        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.SceneAndObjects);
//        fpp.addFilter(bloom);
//        ColorOverlayFilter tension = new ColorOverlayFilter(new ColorRGBA(1, 0, 0, 0.1f));
//        fpp.addFilter(tension);
    }

    public static void initSky(AssetManager assetManager, Node root) {
//        root.attachChild(SkyFactory.createSky(assetManager,
//                assetManager.loadTexture("assets/Textures/outroom.jpg"),
//                assetManager.loadTexture("assets/Textures/outroom.jpg"),
//                assetManager.loadTexture("assets/Textures/outroom.jpg"),
//                assetManager.loadTexture("assets/Textures/outroom.jpg"),
//                assetManager.loadTexture("assets/Textures/outroom.jpg"),
//                assetManager.loadTexture("assets/Textures/outroom.jpg")));
    }

    private static void createSparkEmitter(Vector3f position, @NonNull Node node, AssetManager assetManager, String name) {
        ParticleEmitter emitter = new ParticleEmitter(name, ParticleMesh.Type.Triangle, 100);
        Material sparkMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        sparkMat.setTexture("Texture", assetManager.loadTexture("assets/Effects/spark.jpg"));

        emitter.setLocalTranslation(position);
        emitter.setMaterial(sparkMat);
        emitter.setImagesX(1);
        emitter.setImagesY(1);
        emitter.setSelectRandomImage(true);
        emitter.setRandomAngle(true);
        emitter.setRotateSpeed(5);
        emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0.7f, 0.7f, 0.7f));
        emitter.getParticleInfluencer().setVelocityVariation(1);
        emitter.setStartColor(ColorRGBA.Yellow);
        emitter.setEndColor(ColorRGBA.Red);
        emitter.setFacingVelocity(true);
        emitter.setStartSize(0.08f);
        emitter.setEndSize(0.08f);
        emitter.setLowLife(5);
        emitter.setHighLife(8);

        node.attachChild(emitter);
    }
}
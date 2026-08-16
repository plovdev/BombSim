package com.plovdev.bombsim.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.plovdev.bombsim.audio.AudioManager;
import com.plovdev.bombsim.events.BombLoopFinished;
import com.plovdev.bombsim.events.GlobalEventManager;
import com.plovdev.bombsim.utils.Globals;
import de.lessvoid.nifty.Nifty;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BombPlantedAppState extends BaseAppState {
    private static final float TOTAL_TIME = 40.0f;
    private static final Logger log = LoggerFactory.getLogger(BombPlantedAppState.class);
    private static final ScheduledExecutorService diodDisablerExecutor = Executors.newSingleThreadScheduledExecutor();

    static {
        Globals.addShutdownHook(diodDisablerExecutor::close);
    }

    private final Node bombNode;
    private final Geometry diod;
    private final PointLight diodLight;
    private final Nifty nifty;

    private final Material diodMat;
    private Texture diodOn;
    private Texture diodOff;

    private Node rootNode;
    private SimpleApplication application;
    private InputManager inputManager;
    private AssetManager assetManager;
    private Camera camera;
    private AudioManager audioManager;

    private float timeLeft = TOTAL_TIME;
    private float ticksTimer = 0.0f;
    private float secondsTimer = 0.0f;
    private float speedScaleFactor = 1f;
    private float defuseTimeLeft = 10;
    private float callbackTimer = 0;

    private boolean canCallbackTriggs = false;
    private volatile boolean isActive = false;
    private volatile boolean isNeedDefuse = false;

    public BombPlantedAppState(@NonNull Node bombNode, Nifty nifty) {
        this.bombNode = bombNode;
        this.nifty = nifty;
        this.diod = (Geometry) bombNode.getChild("Diod_0");
        this.diodLight = new PointLight(diod.getLocalTranslation(), ColorRGBA.Red.multLocal(100), 1.5f);
        this.diodMat = diod.getMaterial();
    }

    @Override
    protected void initialize(@NonNull Application app) {
        this.application = (SimpleApplication) app;
        this.inputManager = application.getInputManager();
        this.assetManager = application.getAssetManager();
        this.camera = application.getCamera();
        this.audioManager = app.getStateManager().getState(AudioManager.class);
        this.rootNode = application.getRootNode();

        this.diodOn = assetManager.loadTexture("assets/Textures/light_on.png");
        this.diodOff = assetManager.loadTexture("assets/Textures/light_off.png");
    }

    @Override
    protected void cleanup(Application application) {
        reset();
    }

    @Override
    protected void onEnable() {
    }

    public void plant() {
        log.info("Planting the bomb");
        isActive = true;
        audioManager.playPlant(rootNode);
    }

    @Override
    protected void onDisable() {
    }

    public void dDefuse() {
        log.info("Defusing the bomb");
        isNeedDefuse = true;
    }

    public void aDefuse() {
        audioManager.playADefuse(rootNode);
        callbackTimer = 5;
        defuse();
    }

    private void defuse() {
        reset();
        canCallbackTriggs = true;
        log.info("Bomb has been defused.");
    }

    @Override
    public void update(float tpf) {
        if (!isEnabled()) return;

        if (canCallbackTriggs) {
            callbackTimer -= tpf;
            if (callbackTimer <= 0) {
                log.info("Triggering callback");
                canCallbackTriggs = false;
                GlobalEventManager.broadcastEvent(new BombLoopFinished());
            }
        }

        if (!isActive) return;

        timeLeft -= tpf;
        if (timeLeft <= 0) {
            audioManager.playExplode(bombNode);
            reset();
            callbackTimer = 10;
            canCallbackTriggs = true;
            log.info("Bomb is explosed");
            //TODO: show explosion
        } else {
            secondsTimer += tpf;
            if (secondsTimer >= 1) {
                secondsTimer = 0;
                speedScaleFactor += 0.1f;
                System.out.print("\rTime left: " + ((int) timeLeft));
                //TODO: update UI timer
            }

            ticksTimer += tpf * speedScaleFactor;
            if (ticksTimer >= 1) {
                ticksTimer = 0;
                audioManager.playTick(bombNode);
                enableDiodLight();
            }
        }

        if (isNeedDefuse) {
            defuseTimeLeft -= tpf;
            if (defuseTimeLeft <= 0) {
                audioManager.playDDefuse(rootNode);
                callbackTimer = 5;
                defuse();
            }
        }
    }

    private void enableDiodLight() {
        if (diodLight != null) {
            diodMat.setTexture("DiffuseMap", diodOn);
            rootNode.addLight(diodLight);
            diodDisablerExecutor.schedule(() -> application.enqueue(() -> {
                rootNode.removeLight(diodLight);
                diodMat.setTexture("DiffuseMap", diodOff);
            }), 200, TimeUnit.MILLISECONDS);
        }
    }

    public void reset() {
        timeLeft = TOTAL_TIME;
        ticksTimer = 0;
        secondsTimer = 0;
        speedScaleFactor = 1;
        isActive = false;
        isNeedDefuse = false;
        defuseTimeLeft = 10;
        callbackTimer = 0;
        canCallbackTriggs = false;
        //TODO: clear UI timer
    }
}
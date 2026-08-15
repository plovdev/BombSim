package com.plovdev.bombsim;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FadeFilter;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.plovdev.bombsim.controls.BombControl;
import com.plovdev.bombsim.events.BombModelChangeEvent;
import com.plovdev.bombsim.events.GlobalEventManager;
import com.plovdev.bombsim.states.GameAppState;
import com.plovdev.bombsim.states.GameScreensUpdaterState;
import com.plovdev.bombsim.states.MenuAppState;
import com.plovdev.bombsim.utils.PreferencesStorage;
import com.plovdev.bombsim.utils.Utils;
import de.lessvoid.nifty.Nifty;
import org.jspecify.annotations.NonNull;
import org.plovdev.eda.reflect.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BombSim3D extends SimpleApplication {
    private static final Logger log = LoggerFactory.getLogger(BombSim3D.class);

    private final FadeFilter statesFade = new FadeFilter(1);
    private Nifty nifty;
    private NiftyJmeDisplay display;
    private Node bombModel;
    private FilterPostProcessor fpp;

    private MenuAppState menuAppState;
    private GameAppState gameAppState;

    public BombSim3D(AppSettings settings) {
        setSettings(settings);
    }

    @Override
    public void simpleInitApp() {
        // Init world
        fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(statesFade);
        viewPort.addProcessor(fpp);
        reattachBomb(new BombModelChangeEvent(PreferencesStorage.get("current-model", "assets/Models/Bomb-yellow_SCREEN.glb")));

        display = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = display.getNifty();
        guiViewPort.addProcessor(display);
        BombSimInitializer.init(this, fpp, nifty);

        // Init states
        menuAppState = new MenuAppState(bombModel, nifty);
        gameAppState = new GameAppState(bombModel, nifty);
        gameAppState.setEnabled(false);

        stateManager.attach(menuAppState);
        stateManager.attach(gameAppState);

        GameScreensUpdaterState gameScreensUpdaterState = new GameScreensUpdaterState(nifty, statesFade);
        GlobalEventManager.registerListener(gameScreensUpdaterState);
        stateManager.attach(gameScreensUpdaterState);
    }

    @Subscribe(channel = GlobalEventManager.BOMB_MODEL_CHANGE_EVENT)
    private void reattachBomb(@NonNull BombModelChangeEvent event) {
        String path = event.getNewModel();
        log.debug("Loading bomb model: {}", path);
        try {
            if (bombModel != null) {
                bombModel.removeControl(BombControl.class);
                bombModel.removeFromParent();
            }
            bombModel = (Node) assetManager.loadModel(path);
            if (bombModel != null) {
                rootNode.attachChild(bombModel);
                Utils.prepareModel(bombModel);
            }
        } catch (Exception e) {
            log.error("Error to load bomb model: {}", path, e);
        }
    }

    @Override
    public void destroy() {
        log.info("Stopping engine. Saving settings and cleanup resources...");
        PreferencesStorage.savePreferences();
        super.destroy();
    }
}
package com.plovdev.bombsim;

import com.jme3.app.SimpleApplication;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.plovdev.bombsim.controls.BombControl;
import com.plovdev.bombsim.events.EventManager;
import com.plovdev.bombsim.events.impls.BombModel;
import com.plovdev.bombsim.events.impls.ModelChangeEventListener;
import com.plovdev.bombsim.gui.controls.MainScreenControl;
import com.plovdev.bombsim.states.GameAppState;
import com.plovdev.bombsim.states.MenuAppState;
import de.lessvoid.nifty.Nifty;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.Preferences;

public class BombSim3D extends SimpleApplication {
    private final Logger log = LoggerFactory.getLogger(BombSim3D.class);
    private final Preferences prefs = Preferences.userRoot().node("BombSim").node("Settings");
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
        viewPort.addProcessor(fpp);
        BombSimInitializer.init(this, fpp);

        // Init GUI
        display = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = display.getNifty();
        guiViewPort.addProcessor(display);
        nifty.addXml("assets/Interface/screens/sound.xml");
        nifty.fromXml("assets/Interface/screens/main.xml", "main", new MainScreenControl());

        // Init Model
        reattachBomb(prefs.get("current-model", "assets/Models/Bomb-yellow_SCREEN.glb"));
        prepareModel(bombModel);
        EventManager.getInstance().subscribe(new ModelChangeEventListener(e -> {
            if (e instanceof BombModel model) {
                reattachBomb(model.getPath());
            }
        }));

        // Init states
        menuAppState = new MenuAppState(bombModel);
        gameAppState = new GameAppState(bombModel);
        //gameAppState.setEnabled(false);

        stateManager.attach(menuAppState);
        stateManager.attach(gameAppState);
    }

    private void reattachBomb(String path) {
        try {
            if (bombModel != null) {
                bombModel.removeControl(BombControl.class);
                bombModel.removeFromParent();
            }
            bombModel = (Node) assetManager.loadModel(path);
            if (bombModel != null) {
                rootNode.attachChild(bombModel);
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки модели: {}", path, e);
        }
    }

    private void prepareModel(@NonNull Node bomb) {
        bomb.depthFirstTraversal(s -> {
            String name = s.getName();
            if (name == null) {
                return;
            }
            if (name.startsWith("Button")) {
                if (name.contains("ButtonsPane")) return;
                s.setUserData("Number", name.substring(6).replace("_0", "").replace("Node", ""));
            }
        });
    }
}
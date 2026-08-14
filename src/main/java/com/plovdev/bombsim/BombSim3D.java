package com.plovdev.bombsim;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.plovdev.bombsim.controls.BombControl;
import com.plovdev.bombsim.controls.gui.MainScreenControl;
import com.plovdev.bombsim.controls.gui.MenuScreenControl;
import com.plovdev.bombsim.controls.gui.SettingsScreenControl;
import com.plovdev.bombsim.events.EventManager;
import com.plovdev.bombsim.events.impls.BombModel;
import com.plovdev.bombsim.events.impls.ModelChangeEventListener;
import com.plovdev.bombsim.states.GameAppState;
import com.plovdev.bombsim.states.MenuAppState;
import com.plovdev.bombsim.utils.PreferencesStorage;
import de.lessvoid.nifty.Nifty;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BombSim3D extends SimpleApplication {
    private final Logger log = LoggerFactory.getLogger(BombSim3D.class);
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
        nifty.registerScreenController(new MenuScreenControl(this), new MainScreenControl(), new SettingsScreenControl());
        nifty.addXml("assets/Interface/screens/menu.xml");
        nifty.addXml("assets/Interface/screens/main.xml");
        nifty.addXml("assets/Interface/screens/settings.xml");
        guiViewPort.addProcessor(display);

        // Init Model
        reattachBomb(PreferencesStorage.get("current-model", "assets/Models/Bomb-yellow_SCREEN.glb"));
        prepareModel(bombModel);
        EventManager.getInstance().subscribe(new ModelChangeEventListener(e -> {
            if (e instanceof BombModel model) {
                reattachBomb(model.getPath());
            }
        }));

        // Init states
        menuAppState = new MenuAppState(nifty);
        gameAppState = new GameAppState(bombModel, nifty);
        gameAppState.setEnabled(false);

        stateManager.attach(menuAppState);
        stateManager.attach(gameAppState);

        // Init listeners
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        inputManager.addMapping("BackToMenu", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            if (isPressed) {
                if (gameAppState.isEnabled()) {
                    gameAppState.setEnabled(false);
                    menuAppState.setEnabled(true);
                }
            }
        }, "BackToMenu");
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
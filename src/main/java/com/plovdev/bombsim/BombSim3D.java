package com.plovdev.bombsim;

import com.jme3.app.SimpleApplication;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.plovdev.bombsim.controls.BombControl;
import com.plovdev.bombsim.events.EventManager;
import com.plovdev.bombsim.events.impls.BombModel;
import com.plovdev.bombsim.events.impls.ModelChangeEventListener;
import com.plovdev.bombsim.gui.controls.MainScreenControl;
import de.lessvoid.nifty.Nifty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.prefs.Preferences;

public class BombSim3D extends SimpleApplication {
    private final Logger log = LoggerFactory.getLogger(BombSim3D.class);
    private final Preferences prefs = Preferences.userRoot().node("BombSim").node("Settings");
    private Nifty nifty;
    private NiftyJmeDisplay display;
    private Node bombModel;
    private FilterPostProcessor fpp;

    public Nifty getNifty() {
        return nifty;
    }

    public void setNifty(Nifty nifty) {
        this.nifty = nifty;
    }

    public NiftyJmeDisplay getDisplay() {
        return display;
    }

    public void setDisplay(NiftyJmeDisplay display) {
        this.display = display;
    }

    public Spatial getBombModel() {
        return bombModel;
    }

    public void setBombModel(Node bombModel) {
        this.bombModel = bombModel;
    }

    public FilterPostProcessor getFpp() {
        return fpp;
    }

    public void setFpp(FilterPostProcessor fpp) {
        this.fpp = fpp;
    }

    public BombSim3D(AppSettings settings) {
        setSettings(settings);
    }

    @Override
    public void simpleInitApp() {
        fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);
        BombSimInitializer.init(fpp, rootNode, assetManager);

        display = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = display.getNifty();
        guiViewPort.addProcessor(display);
        nifty.addXml("assets/Interface/screens/sound.xml");
        nifty.fromXml("assets/Interface/screens/main.xml", "main", new MainScreenControl());

        flyCam.setEnabled(false);
        setDisplayFps(false);
        setDisplayStatView(false);

        reattachBomb(prefs.get("current-model", "assets/Models/bomb.glb"));
        prepareModel(bombModel);
        EventManager.getInstance().subscribe(new ModelChangeEventListener(e -> {
            if (e instanceof BombModel model) {
                reattachBomb(model.getPath());
            }
        }));
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
                bombModel.addControl(new BombControl(inputManager, assetManager, cam));
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки модели: {}", path, e);
        }
    }

    private void prepareModel(Node bomb) {
        bomb.depthFirstTraversal(s -> {
            String name = s.getName();
            if (name == null) {
                return;
            }
            if (name.startsWith("Button")) {
                if (name.contains("ButtonsPane")) return;
                System.out.println(name);
                s.setUserData("Number", name.substring(6).replace("_0", "").replace("Node", ""));
            }
        });
        try {
            BinaryExporter.getInstance().save(bomb, new File("src/main/resources/assets/Models/bomb.j3o"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
package com.plovdev.bombsim.states;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import org.jspecify.annotations.NonNull;

public class EventsAppState extends BaseAppState {
    private static final String BTM_KEY = "BackToMenu";
    private final ActionListener btmListener = (name, isPressed, tpf) -> {
        if (!isEnabled()) return;
        if (isPressed) {
            switchStates();
        }
    };

    private InputManager inputManager;
    private AppStateManager stateManager;
    private MenuAppState menuAppState;
    private GameAppState gameAppState;

    @Override
    protected void initialize(@NonNull Application application) {
        this.inputManager = application.getInputManager();
        this.stateManager = application.getStateManager();

        this.menuAppState = stateManager.getState(MenuAppState.class);
        this.gameAppState = stateManager.getState(GameAppState.class);

        inputManager.addMapping(BTM_KEY, new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(btmListener, BTM_KEY);
    }

    private void switchStates() {
        if (gameAppState.isEnabled()) {
            gameAppState.setEnabled(false);
            menuAppState.setEnabled(true);
        }
    }

    @Override
    protected void cleanup(Application application) {
        inputManager.deleteMapping(BTM_KEY);
        inputManager.removeListener(btmListener);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
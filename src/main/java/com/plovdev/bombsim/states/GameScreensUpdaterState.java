package com.plovdev.bombsim.states;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.post.filters.FadeFilter;
import com.plovdev.bombsim.events.BombLoopFinished;
import com.plovdev.bombsim.events.GameStateChangeEvent;
import com.plovdev.bombsim.events.GlobalEventManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.screen.Screen;
import org.jspecify.annotations.NonNull;
import org.plovdev.eda.reflect.Subscribe;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameScreensUpdaterState extends BaseAppState {
    private static final String BTM_KEY = "BackToMenu";
    private static final float FADE_OUT_DURATION = 1f;
    private static final float FADE_IN_DURATION = 2.5f;

    private final AtomicBoolean isBombLoopFinished = new AtomicBoolean(true); // С начала будет новая игра же

    private final ActionListener btmListener = (name, isPressed, tpf) -> {
        if (!isEnabled()) return;
        if (isPressed) {
            GlobalEventManager.broadcastEvent(new GameStateChangeEvent(GameStateChangeEvent.GameState.MENU));
        }
    };

    private InputManager inputManager;
    private AppStateManager stateManager;
    private MenuAppState menuAppState;
    private GameAppState gameAppState;
    private BombPlantedAppState plantedAppState;
    private Screen menuScreen;
    private Button newGameButton;

    private FadeUpdateStep fadeStep = FadeUpdateStep.NONE;
    private float timer = 0f;

    private final Nifty nifty;
    private final FadeFilter fadeFilter;

    public GameScreensUpdaterState(Nifty nifty, FadeFilter fadeFilter) {
        this.nifty = nifty;
        this.fadeFilter = fadeFilter;
    }

    @Override
    protected void initialize(@NonNull Application application) {
        this.inputManager = application.getInputManager();
        this.stateManager = application.getStateManager();

        this.menuAppState = stateManager.getState(MenuAppState.class);
        this.gameAppState = stateManager.getState(GameAppState.class);
        this.plantedAppState = stateManager.getState(BombPlantedAppState.class);
        this.menuScreen = nifty.getScreen("menu");
        this.newGameButton = Objects.requireNonNull(menuScreen).findNiftyControl("newGameButton", Button.class);

        inputManager.addMapping(BTM_KEY, new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(btmListener, BTM_KEY);
    }

    @Subscribe(channel = GlobalEventManager.BOMB_LOOP_FINISHED)
    private void onBombLoopFinished(BombLoopFinished event) {
        isBombLoopFinished.set(true);
        btm();
    }

    @Subscribe(channel = GlobalEventManager.GAME_STATE_EVENT)
    private void onGameStateChanged(@NonNull GameStateChangeEvent event) {
        if (event.getGameState() == GameStateChangeEvent.GameState.GAME) {
            if (isBombLoopFinished.get()) {
                fadeFilter.setDuration(FADE_OUT_DURATION);
                fadeFilter.fadeOut();
                nifty.gotoScreen("empty");
                fadeStep = FadeUpdateStep.UPDATE_STATES_TO_GAME;
                isBombLoopFinished.set(false); // starts new game loop
            } else {
                enterToGame();
            }
        } else if (event.getGameState() == GameStateChangeEvent.GameState.MENU) {
            btm();
        }
    }

    private void btm() {
        if (isBombLoopFinished.get()) {
            setTextOnGameButton("New Game");
            gameAppState.reset();
        } else {
            setTextOnGameButton("Resume");
        }

        gameAppState.setEnabled(false);
        plantedAppState.setEnabled(false);
        menuAppState.setEnabled(true);
    }

    private void enterToGame() {
        menuAppState.setEnabled(false);
        gameAppState.setEnabled(true);
        plantedAppState.setEnabled(true);
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

    private void setTextOnGameButton(String text) {
        if (newGameButton != null) {
            newGameButton.setText(text);
        }
    }

    @Override
    public void update(float tpf) {
        if (!isEnabled() || fadeStep == FadeUpdateStep.NONE) return;

        if (fadeStep == FadeUpdateStep.UPDATE_STATES_TO_GAME) {
            timer += tpf;
            if (timer >= fadeFilter.getDuration()) {
                timer = 0;
                fadeFilter.setDuration(FADE_IN_DURATION);
                fadeFilter.fadeIn();
                enterToGame();
                fadeStep = FadeUpdateStep.NONE;
            }
        }
    }

    private enum FadeUpdateStep {
        NONE, UPDATE_STATES_TO_GAME
    }
}
package com.plovdev.bombsim.events;

import org.plovdev.eda.ChannelEvent;

public class GameStateChangeEvent extends ChannelEvent {
    public enum GameState {
        MENU, GAME
    }

    private GameState gameState;

    public GameStateChangeEvent(GameState gameState) {
        super(GlobalEventManager.GAME_STATE_EVENT);
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
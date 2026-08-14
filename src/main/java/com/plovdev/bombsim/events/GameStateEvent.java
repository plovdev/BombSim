package com.plovdev.bombsim.events;

import org.plovdev.eda.ChannelEvent;

public class GameStateEvent extends ChannelEvent {
    public enum GameState {
        MENU, GAME
    }

    private GameState gameState;

    public GameStateEvent(GameState gameState) {
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
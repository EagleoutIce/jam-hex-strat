package de.flojo.jam.networking.server.management;

import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.share.GamePhase;
import de.gurkenlabs.litiengine.Game;

public class GameState {

    int currentRound = 0;
    GamePhase phase = GamePhase.INIT;

    PlayerId currentTurn;

    int moneyP1Left = 100;
    int moneyP2Left = 100;

    public GameState() {
        currentTurn = Game.random().choose(PlayerId.values());
    }


    public boolean p1CanDo() {
        return moneyP1Left > 0;
    }

    public boolean p2CanDo() {
        return moneyP2Left > 0;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void nextRound() {
        this.currentRound += 1;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public PlayerId getCurrentTurn() {
        return currentTurn;
    }

    public void flipCurrentTurn() {
        this.currentTurn = currentTurn.ifOne(PlayerId.TWO, PlayerId.ONE);
    }

    public int getMoneyP1Left() {
        return moneyP1Left;
    }

    public void reduceMoneyP1(int moneyP1Left) {
        this.moneyP1Left -= moneyP1Left;
    }

    public int getMoneyP2Left() {
        return moneyP2Left;
    }

    public void reduceMoneyP2(int moneyP2Left) {
        this.moneyP2Left -= moneyP2Left;
    }

}

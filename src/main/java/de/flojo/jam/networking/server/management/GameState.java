package de.flojo.jam.networking.server.management;

import de.flojo.jam.game.creature.CreatureCollection;
import de.flojo.jam.game.player.PlayerId;
import de.gurkenlabs.litiengine.Game;

public class GameState {
    int currentRound = 0;

    PlayerId currentTurn;

    int moneyP1Left;
    int moneyP2Left;

    public GameState(final int startMoney) {
        currentTurn = Game.random().choose(PlayerId.values());
        moneyP1Left = startMoney;
        moneyP2Left = startMoney;
    }

    public boolean p1CanDoBuild() {
        return moneyP1Left > 0;
    }

    public boolean p2CanDoBuild() {
        return moneyP2Left > 0;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void nextRound() {
        this.currentRound += 1;
    }

    public PlayerId getCurrentTurn() {
        return currentTurn;
    }

    public int getMoneyP1Left() {
        return moneyP1Left;
    }

    public void reduceMoney(PlayerId player, int relative) {
        if (player == PlayerId.ONE)
            reduceMoneyP1(relative);
        else if (player == PlayerId.TWO)
            reduceMoneyP2(relative);
    }


    public void reduceMoneyP1(int relative) {
        this.moneyP1Left -= relative;
    }

    public int getMoneyP2Left() {
        return moneyP2Left;
    }

    public void reduceMoneyP2(int relative) {
        this.moneyP2Left -= relative;
    }

    public void nextPlayer(CreatureCollection collection) {
        if (currentTurn == PlayerId.ONE && collection.p2CanDoSomething()) {
            currentTurn = PlayerId.TWO;
        } else if (currentTurn == PlayerId.TWO && collection.p1CanDoSomething()) {
            currentTurn = PlayerId.ONE;
        }
    }

    public boolean nextBuild() {
        if (currentTurn == PlayerId.ONE && p2CanDoBuild()) {
            currentTurn = PlayerId.TWO;
            return true;
        } else if (currentTurn == PlayerId.TWO && p1CanDoBuild()) {
            currentTurn = PlayerId.ONE;
            return true;
        }
        return p1CanDoBuild() || p2CanDoBuild();
    }

}

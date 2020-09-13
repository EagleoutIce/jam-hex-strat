package de.flojo.jam.networking.server;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.terrain.management.Terrain;

public class MainGameControl {

    private ServerController controller;
    private Board board;
    public MainGameControl(ServerController controller, Board board) {  
        this.controller = controller;
        this.board = board;
    }

    public Terrain getTerrain() {
        return board.getTerrainMap().getTerrain();
    }

}

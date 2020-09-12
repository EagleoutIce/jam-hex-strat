package de.flojo.jam.game.board.terrain;

import de.flojo.jam.game.board.Board;

// Plants and removes terrains from a board and checks for validity
public class Architect {
    

    private final Board board;
    
    public Architect(Board board) {
        this.board = board;
    }



    private void clearField() {
        // final TerrainData data = board.getTerrainMap().getTerrain().getData();
        // // to avoid previous errors we will overwrite and expect existing!
        // for (int i = 0; i < BOARD_HEIGHT; i++) {
        //     List<TerrainType> line = new ArrayList<>(BOARD_WIDTH);
        //     for (int j = 0; j < BOARD_WIDTH; j++) {
        //         line.add(TerrainType.EMPTY);
        //     }
        // }
        // editTerrain = new TerrainMap(new Terrain("Empty Klaus", terrainData));
        // board.setTerrainMap(editTerrain);
    }


}

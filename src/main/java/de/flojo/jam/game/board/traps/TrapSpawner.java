package de.flojo.jam.game.board.traps;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.InputController;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

public class TrapSpawner {

    private final Board board;

    private final TrapCollection traps;

    private Trap selectedTrap;

    public TrapSpawner(final Board board, final String screen) {
        this.board = board;
        this.traps = new TrapCollection();
        InputController.get().onClicked(this::setActiveTrap, screen);
    }

    public static void injectTrap(TrapId id, PlayerId owner, Tile tile, Board board, TrapCollection traps) {
        if (traps.get(tile.getCoordinate()).isPresent())
            return;
        final var trap = new Trap(board, owner, Objects.requireNonNull(id, "Cannot inject trap without an id (TrapId)"),
                             tile);
        traps.add(trap);
        HexStratLogger.log().log(Level.INFO, "Injected trap with id \"{0}\" at {1} with Id \"{2}\"",
                                 new Object[]{id, tile, owner});
    }

    private void setActiveTrap(MouseEvent c) {
        if (c.getButton() != MouseEvent.BUTTON1)
            return;

        final var oldTrap = selectedTrap;
        this.selectedTrap = traps.getHighlighted().orElse(null);
        if (oldTrap != selectedTrap)
            HexStratLogger.log().log(Level.INFO, "Selected Trap: {0}.", this.selectedTrap);
    }

    public void spawnTrap(TrapId id, PlayerId owner, Tile tile) {
        if (traps.get(tile.getCoordinate()).isPresent())
            return;
        final var trap = new Trap(board, owner, Objects.requireNonNull(id, "Cannot spawn trap without an id (TrapId)"),
                             tile);
        traps.add(trap);
        HexStratLogger.log().log(Level.INFO, "Spawned trap with id \"{0}\" at {1} with Id \"{2}\"",
                                 new Object[]{id, tile, owner});
    }

    public Trap getSelectedTrap() {
        return selectedTrap;
    }

    public void removeTrap(Tile onBase) {
        traps.removeIf(t -> t.coversTile(onBase.getCoordinate()));
    }

    public TrapCollection getTraps() {
        return traps;
    }

    private enum TileElevation {
            ELEVATED, GROUND
    }

    public boolean canBePlaced(CreatureFactory creatures, TrapId id, Tile pos, PlayerId playerId, Board board) {
        final var tiles = Trap.getEffectiveTiles(id.getImprint(), pos, board);
        TileElevation elevated = null;
        for (var tile : tiles) {
            // TODO: this is hacky
            if (tile == null)
                return false;
            if (tile.getTerrainType().equals(TerrainTile.EMPTY)) {
                if(elevated == null)
                    elevated = TileElevation.GROUND;
                else if (elevated == TileElevation.ELEVATED)
                    return false;
            }
            else if (tile.getTerrainType().equals(TerrainTile.GRASS_HILL)) {
                if (elevated == null)
                    elevated = TileElevation.ELEVATED;
                else if (elevated == TileElevation.GROUND) {
                    return false;
                }
            } else {
                return false;
            }

            if (playerId != null && tile.getPlacementOwner() != playerId)
                return false;
        }
        return traps.getCollision(tiles).isEmpty() && creatures.get(tiles).isEmpty();
    }


    public Optional<Trap> get(BoardCoordinate coordinate) {
        return traps.get(coordinate);
    }

    // only returns on root tile
    public Optional<Trap> getRoot(BoardCoordinate coordinate) {
        return traps.getRoot(coordinate);
    }

    public boolean isEmpty() {
        return traps.isEmpty();
    }

    public int size() {
        return traps.size();
    }

    public void removeAll() {
        this.traps.clear();
    }

    public void updateTraps(List<TrapJson> nT) {
        traps.clear();
        for (TrapJson tJ : nT) {
            final var deserialized = new Trap(board, tJ.getOwner(), tJ.getId(), board.getTile(tJ.getPos()));
            traps.add(deserialized);
        }
    }
}

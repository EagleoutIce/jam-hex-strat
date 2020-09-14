package de.flojo.jam.game.board.traps;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.management.TrapData;
import de.flojo.jam.game.player.PlayerId;
import de.gurkenlabs.litiengine.Game;

public class Trap  {
    
    private static final int TRAP_DURATION = 1000;

    private final TrapId trapId;
    private final Tile rootPosition;
    private final Set<Tile> ghosts;

    private final PlayerId owner;

    private boolean enemyDidUncover = false;
    private AtomicBoolean triggered = new AtomicBoolean();

    public Trap(Board board, PlayerId owner, TrapId id, Tile rootPosition) {
        this.owner = owner;
        this.trapId = id;
        this.rootPosition = rootPosition;
        this.ghosts = new HashSet<>();
        populateGhosts(board);
    }

    private void populateGhosts(final Board board) {
        TrapData data = getImprint().getData();
        Point anchor = getImprint().getAnchor();
        for (int y = 0; y < data.getHeight(); y++) {
            for (int x = 0; x < data.getHeight(); x++) {
                if(data.getTrapTileAt(x, y).isPresent()) {
                    BoardCoordinate effectiveCoordinate = rootPosition.getCoordinate().translateRelativeX(x - anchor.x, y - anchor.y);
                    ghosts.add(board.getTile(effectiveCoordinate));
                }
            }
        }
    }

    public Tile getRootPosition() {
        return rootPosition;
    }
    
    public TrapImprint getImprint() {
        return trapId.getImprint();
    }

    public String getName() {
        return trapId.getName();
    }

    public boolean coversTile(BoardCoordinate coordinate){
        for (Tile tile : ghosts) {
            if(Objects.equals(tile.getCoordinate(), coordinate))
                return true;
        }
        return false;
    }

    public void trigger() {
        enemyDidUncover = true;
        triggered.set(true);
        Game.loop().perform(TRAP_DURATION, this::untrigger);
    }

    private void untrigger() {
        triggered.set(false);
    }

    public boolean isHovered() {
        for (Tile tile : ghosts) {
            if(tile.isHovered())
                return true;
        }
        return false;
    }

    public void renderBaseFor(Graphics2D g, PlayerId id) {
        if(id != null && id != owner && !enemyDidUncover) 
            return;
        getImprint().getNormalRenderer().render(g, rootPosition.getCenter(), isHovered());
    }

    public void renderTriggerFor(Graphics2D g, PlayerId id) {
        if(id != null && id != owner && !enemyDidUncover) 
            return;
        if(!triggered.get())
            return;
        getImprint().getTriggeredRenderer().render(g, rootPosition.getCenter(), isHovered());
    }

	public BoardCoordinate getCoordinate() {
		return rootPosition.getCoordinate();
	}

    public int getAnimationCooldown() {
        return trapId.getAnimationCooldown();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Trap [enemyDidUncover=").append(enemyDidUncover).append(", ghosts=").append(ghosts)
                .append(", owner=").append(owner).append(", rootPosition=").append(rootPosition).append(", trapId=")
                .append(trapId).append(", triggered=").append(triggered).append("]");
        return builder.toString();
    }

}

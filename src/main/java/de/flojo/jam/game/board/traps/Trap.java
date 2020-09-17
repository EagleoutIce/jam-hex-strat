package de.flojo.jam.game.board.traps;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.imprints.Imprint;
import de.flojo.jam.game.player.PlayerId;
import de.gurkenlabs.litiengine.Game;

public class Trap implements Serializable {
    
    private static final long serialVersionUID = 2185724284978150753L;

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
        ghosts = getEffectiveTiles(trapId.getImprint(), rootPosition, board);
    }

    public static Set<Tile> getEffectiveTiles(Imprint<?> imprint, Tile pos, Board board) {
        Set<Tile> ghosts = new HashSet<>();
        BufferedImage data = imprint.getBitMap();
        Point anchor = imprint.getAnchor();
        for (int y = 0; y < data.getHeight(); y++) {
            for (int x = 0; x < data.getHeight(); x++) {
                if(imprint.isSet(x, y)) {
                    BoardCoordinate effectiveCoordinate = pos.getCoordinate().translateRelativeX(x - anchor.x, y - anchor.y);
                    ghosts.add(board.getTile(effectiveCoordinate));
                }
            }
        }
        return ghosts;
    }

    public Tile getRootPosition() {
        return rootPosition;
    }

    public TrapId getTrapId() {
        return trapId;
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

    public boolean collidesWith(Set<Tile> tiles) {
        for (Tile t : tiles) {
            if(coversTile(t.getCoordinate()))
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
		return getRootPosition().getCoordinate();
	}

    public int getAnimationCooldown() {
        return trapId.getAnimationCooldown();
    }

    public PlayerId getOwner() {
        return owner;
    }

    public boolean uncoveredByEnemy() {
        return enemyDidUncover;
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

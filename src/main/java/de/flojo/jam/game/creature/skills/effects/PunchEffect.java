package de.flojo.jam.game.creature.skills.effects;

import java.util.Optional;
import java.util.logging.Level;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.IEffectCreature;
import de.flojo.jam.game.creature.skills.IProvideEffectContext;
import de.gurkenlabs.litiengine.Game;

public class PunchEffect implements IEffectCreature {

    private int powerLeft;
    private final IProvideEffectContext context;

    private boolean deltaSet = false;
    private int deltaX;
    private int deltaY;

    public PunchEffect(IProvideEffectContext context, int totalPower) {
        this.powerLeft = totalPower;
        this.context = context;
    }

    private int normalizeDistDelta(int a, int b) {
        if (a == b)
            return 0;
        return a > b ? 1 : -1;
    }

    // ug 7/20 1/-1
    private BoardCoordinate decodeDelta(BoardCoordinate a, BoardCoordinate t) {
        if (a.x == t.x && a.y == t.y)
            return new BoardCoordinate(0, 0);

        int dX = normalizeDistDelta(t.x, a.x);
        int dY = normalizeDistDelta(t.y, a.y);
        // TODO: guard if not 1?
        int mX = Math.floorMod(t.x, 2);
        int mY = Math.floorMod(t.y, 2);

        if (dX == 0) {
            return getDeltaForNoX(dY, mY, mX);
        } else if (dX > 0) {
            if (dY < 0) {
                return new BoardCoordinate(dX, -1);
            } else if (dY == 0) {
                Game.log().log(Level.SEVERE, "dX > 0 && dY == 0 steppe from a {0}  to b {1}", new Object[] { a, t });
            }
        } else if (dY == 0) {
            Game.log().log(Level.SEVERE, "dX > 0 && dY == 0 steppe from a {0}  to b {1}", new Object[] { a, t });
        }
        return new BoardCoordinate(dX, dY);

    }

    // ungerade ungerade y < kleiner => x < kleiner
    // gerade ungerade y < kleiner => x > größer ||| 10 9
    // ungerade gerade y kleiner => x größer
    // gerade gerade, y kleiner => x größer
    private BoardCoordinate getDeltaForNoX(int dY, int mY, int mX) {
        if (dY < 0)
            return new BoardCoordinate(mY == 1 && mX == 1 ? -1 : 1, dY);
        else
            return new BoardCoordinate(mY == 0 ? 1 : -1, dY);
    }

    @Override
    public void effect(Creature target, Creature attacker) {
        // done
        if (powerLeft <= 0)
            return;

        if (!deltaSet) {
            BoardCoordinate delta = decodeDelta(attacker.getCoordinate(), target.getCoordinate());
            deltaX = delta.x;
            deltaY = delta.y;
            deltaSet = true;
        }

        // Peek next tile
        BoardCoordinate punchTargetCoordinate = target.getCoordinate().translateRelativeX(deltaX, deltaY);
        Tile punchTarget = context.getBoard().getTile(punchTargetCoordinate);

        // if no more on field exit
        if (punchTarget == null)
            return;

        if (punchTarget.getTerrainType().blocksWalking()) {
            Game.log().log(Level.INFO, "Push for dx/dy: {0}/{1} stopped for {2} at {3} as it blocks walking.",
                    new Object[] { deltaX, deltaY, target.getName(), punchTarget });
            return;
        }

        // URGENT TODO: traps

        Optional<Creature> mayHit = context.getCreatures().get(punchTargetCoordinate);
        if (mayHit.isPresent()) {
            awaitMovementComplete(target);
            effect(mayHit.get(), attacker);
        } else {
            // punch!
            target.move(punchTarget);
            powerLeft -= 1;

            effect(target, attacker);
        }
    }

    private void awaitMovementComplete(Creature target) {
        synchronized (target.moveLock()) {
            while (!target.getBase().moveTargetReached()) {
                try {
                    target.moveLock().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}

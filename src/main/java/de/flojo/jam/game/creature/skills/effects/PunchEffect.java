package de.flojo.jam.game.creature.skills.effects;

import java.util.Optional;
import java.util.logging.Level;

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

    @Override
    public void effect(Creature target, Creature attacker) {
        // done
        if(powerLeft <= 0) 
            return;

        if (!deltaSet) {
            deltaX = target.getX() - attacker.getX();
            deltaY = target.getY() - attacker.getY();
        }

        // Peek next tile
        Tile punchTarget = context.getBoard().getTile(target.getCoordinate().getShifted(deltaX, deltaY));
        if (punchTarget.getTerrainType().blocksWalking()) {
            Game.log().log(Level.INFO, "Push for dx/dy: {0}/{1} stopped for {2} at {3} as it blocks walking.",
                    new Object[] { deltaX, deltaY, target.getName(), punchTarget });
            return;
        } 
        
        Optional<Creature> mayHit = context.getCreatures().findAt(target.getCoordinate());
        if(mayHit.isPresent()) {
            effect(mayHit.get(), attacker);
        } else {
            // punch!
            target.move(punchTarget);
            powerLeft -= 1;
            effect(target, attacker);
        }
    }

}

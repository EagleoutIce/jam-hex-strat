package de.flojo.jam.game.creature.skills.effects;

import java.util.Optional;
import java.util.logging.Level;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.Trap;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.controller.CreatureActionController;
import de.flojo.jam.game.creature.skills.IEffectCreature;
import de.flojo.jam.game.creature.skills.IProvideEffectContext;
import de.flojo.jam.util.HexMaths;
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
        if (powerLeft <= 0)
            return;

        if (!deltaSet) {
            BoardCoordinate delta = HexMaths.decodeDelta(attacker.getCoordinate(), target.getCoordinate());
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

        if (punchTarget.getTerrainType().blocksPunching()) {
            Game.log().log(Level.INFO, "Push for dx/dy: {0}/{1} stopped for {2} at {3} as it blocks punching.",
                    new Object[] { deltaX, deltaY, target.getName(), punchTarget });
            return;
        }


        Optional<Trap> mayTrap = context.getTraps().get(punchTargetCoordinate);

        if(mayTrap.isPresent()) {
            Trap trap = mayTrap.get();
            target.move(punchTarget);
            try {
                CreatureActionController.awaitMovementComplete(target);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            trap.trigger();
            target.die();
            return;
        }

        // URGENT TODO: traps

        Optional<Creature> mayHit = context.getCreatures().get(punchTargetCoordinate);
        if (mayHit.isPresent()) {
            CreatureActionController.awaitMovementComplete(target);
            effect(mayHit.get(), attacker);
        } else {
            // punch!
            target.move(punchTarget);
            powerLeft -= 1;
            effect(target, attacker);
        }
    }

}

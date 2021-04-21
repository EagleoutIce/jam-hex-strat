package de.flojo.jam.game.creature.skills.effects;

import de.flojo.jam.game.board.traps.Trap;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.controller.CreatureActionController;
import de.flojo.jam.game.creature.skills.IEffectTarget;
import de.flojo.jam.game.creature.skills.IProvideReadContext;
import de.flojo.jam.util.HexStratLogger;

import java.util.Optional;
import java.util.logging.Level;

public class ToggleFlyEffect implements IEffectTarget {

    private final IProvideReadContext context;

    public ToggleFlyEffect(IProvideReadContext context) {
        this.context = context;
    }

    @Override
    public void effect(Creature target, Creature attacker) {
        boolean nowFlying = attacker.getCore().toggleFly();
        if (!nowFlying) {
            // Check if the creature landed on a mine:
            Optional<Trap> mayTrap = context.getTraps().get(target.getCoordinate());
            mayTrap.ifPresent(trap -> {
                target.moribund();
                new Thread(() -> trapExecution(target, trap)).start();
            });
        }
    }

    private void trapExecution(Creature target, Trap trap) {
        HexStratLogger.log().log(Level.INFO, "Creature {0} dies by landing on trap {1}", new Object[]{target, trap});
        trap.trigger();
        CreatureActionController.sleep(trap.getAnimationCooldown());
        target.die();
    }
}

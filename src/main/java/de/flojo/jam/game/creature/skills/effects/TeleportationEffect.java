package de.flojo.jam.game.creature.skills.effects;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.Trap;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.controller.CreatureActionController;
import de.flojo.jam.game.creature.skills.IEffectTarget;
import de.flojo.jam.game.creature.skills.IProvideReadContext;

import java.util.Optional;

public class TeleportationEffect implements IEffectTarget {

    private final IProvideReadContext context;

    public TeleportationEffect(IProvideReadContext context) {
        this.context = context;
    }

    private void trapExecution(Creature target, Tile punchTarget, Trap trap) {
        target.moribund();
        target.moveBlocking(punchTarget);
        trap.trigger();
        CreatureActionController.sleep(trap.getAnimationCooldown());
        target.die();
    }

    @Override
    public void effect(Tile target, Creature attacker) {
        attacker.getBase().setPosition(target);

        Optional<Trap> mayTrap = context.getTraps().get(target.getCoordinate());

        mayTrap.ifPresent(trap -> new Thread(() -> trapExecution(attacker, target, trap)).start());
    }

}

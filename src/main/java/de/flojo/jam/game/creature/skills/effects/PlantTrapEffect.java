package de.flojo.jam.game.creature.skills.effects;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.IEffectTarget;
import de.flojo.jam.game.creature.skills.IProvideReadContext;

public class PlantTrapEffect implements IEffectTarget {

    private final IProvideReadContext context;

    public PlantTrapEffect(IProvideReadContext context) {
        this.context = context;
    }

    @Override
    public void effect(Tile target, Creature attacker) {
        TrapSpawner.injectTrap(TrapId.T_GOBLIN_TRAP, attacker.getOwner(), target, context.getBoard(),
                               context.getTraps());
    }
}

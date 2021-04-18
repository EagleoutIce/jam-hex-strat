package de.flojo.jam.game.creature.skills.effects;

import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.IEffectCreature;
import de.flojo.jam.game.creature.skills.IProvideEffectContext;
import de.flojo.jam.game.creature.skills.PlantTrap;

public class PlantTrapEffect implements IEffectCreature {

    private final IProvideEffectContext context;

    public PlantTrapEffect(IProvideEffectContext context) {
        this.context = context;
    }

    @Override
    public void effect(Creature target, Creature attacker) {
        // TODO: ground effect
    }
}

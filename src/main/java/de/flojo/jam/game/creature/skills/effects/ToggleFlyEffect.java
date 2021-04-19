package de.flojo.jam.game.creature.skills.effects;

import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.IEffectCreature;
import de.flojo.jam.game.creature.skills.IEffectTarget;

public class ToggleFlyEffect implements IEffectTarget {

    @Override
    public void effect(Creature target, Creature attacker) {
        attacker.getCore().toggleFly();
    }
}

package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.Creature;

@FunctionalInterface
public interface IEffectCreature {
    
    void effect(Creature target, Creature attacker);

}

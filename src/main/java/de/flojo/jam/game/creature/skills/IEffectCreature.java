package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.util.HexStartLogger;

import java.util.logging.Level;

public interface IEffectCreature {
    default void effect(Creature target, Creature attacker) {
        HexStartLogger.log().log(Level.SEVERE,"Unsupported effect target with {0} for attacker {1}", new Object[]{target, attacker});
    }
}

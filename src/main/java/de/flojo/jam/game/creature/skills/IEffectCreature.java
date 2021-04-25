package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.util.HexStratLogger;

import java.util.logging.Level;

public interface IEffectCreature {
    default void effect(final Creature target, final IEffectAttackerInformation attacker) {
        HexStratLogger.log().log(Level.SEVERE, "Unsupported effect target with {0} for attacker {1}",
                                 new Object[]{target, attacker});
    }
}

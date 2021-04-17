package de.flojo.jam.game.creature.skills.effects;

import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.IEffectCreature;

public class ToggleFlyEffect implements IEffectCreature {

	@Override
	public void effect(Creature target, Creature attacker) {
		attacker.getCore().toggleFly();
	}
}

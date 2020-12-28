package de.flojo.jam.game.creature.creatures;

import java.util.HashSet;
import java.util.Set;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.CreatureAttributes;
import de.flojo.jam.game.creature.CreatureBase;
import de.flojo.jam.game.creature.CreatureCollection;
import de.flojo.jam.game.creature.CreatureCore;
import de.flojo.jam.game.creature.CreatureId;
import de.flojo.jam.game.creature.skills.ICreatureSkill;
import de.flojo.jam.game.creature.skills.SimplePunch;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;

public class CreatureGoblin extends Creature {

	public CreatureGoblin(String name, Tile startBase, PlayerId playerId, CreatureCollection cCollection, TrapCollection tCollection, IRenderData normal, IRenderData dying) {
		super(CreatureId.GOBLIN, name, cCollection, tCollection, new CreatureBase(startBase), createGoblinCore(playerId, normal, dying));
	}

	private static CreatureCore createGoblinCore(PlayerId playerId, IRenderData normal, IRenderData dying) {
		return new CreatureCore(playerId, normal, dying, createGoblinAttributes());
	}

	private static CreatureAttributes createGoblinAttributes() {
		Set<ICreatureSkill> skills = new HashSet<>();
		skills.add(new SimplePunch(1, 0, 1, "Punch (1)", "Ein einfacher Hieb mit Mit dem Ellenbogen des kleinen Goblins."));
		// TODO: flying
		return new CreatureAttributes(3, 1, skills);
	}

}

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

public class CreatureElf extends Creature {

	public CreatureElf(String name, Tile startBase, PlayerId playerId, CreatureCollection cCollection, TrapCollection tCollection, IRenderData normal, IRenderData dying) {
		super(CreatureId.ELF, name, cCollection, tCollection, new CreatureBase(startBase), createElfCore(playerId, normal, dying));
	}

	private static CreatureCore createElfCore(PlayerId playerId, IRenderData normal, IRenderData dying) {
		return new CreatureCore(playerId, normal, dying, createElfAttributes());
	}

	private static CreatureAttributes createElfAttributes() {
		Set<ICreatureSkill> skills = new HashSet<>();
		skills.add(new SimplePunch(2, 1, 3, "Ein mega Schuss", "Ein einfacher Schuss mit mit dem Elfenbogen des kleinen Goblins."));
		// TODO: arrows
		return new CreatureAttributes(4, 1, skills);
	}
}

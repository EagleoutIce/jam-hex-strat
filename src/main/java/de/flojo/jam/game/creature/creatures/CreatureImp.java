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
import de.flojo.jam.game.creature.skills.ToggleFly;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;

public class CreatureImp extends Creature {

	public CreatureImp(String name, Tile startBase, PlayerId playerId, CreatureCollection cCollection, TrapCollection tCollection, IRenderData normal, IRenderData dying) {
		super(CreatureId.IMP, name, cCollection, tCollection, new CreatureBase(startBase), createImpCore(playerId, normal, dying));
	}

	private static CreatureCore createImpCore(PlayerId playerId, IRenderData normal, IRenderData dying) {
		return new CreatureCore(playerId, normal, dying, createImpAttributes());
	}

	private static CreatureAttributes createImpAttributes() {
		Set<ICreatureSkill> skills = new HashSet<>();
		skills.add(new SimplePunch(1, 0, 1, "Punch (1)", "Ein einfacher Stupser mit dem Ellenbogen."));
		skills.add(new ToggleFly("Fliegen/Landen", "Wechselt den Flugstatus."));
		return new CreatureAttributes(3, 1, skills);
	}

}

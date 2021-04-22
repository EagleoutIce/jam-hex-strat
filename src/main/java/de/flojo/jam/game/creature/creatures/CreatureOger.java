package de.flojo.jam.game.creature.creatures;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.CreatureAttributes;
import de.flojo.jam.game.creature.CreatureBase;
import de.flojo.jam.game.creature.CreatureCollection;
import de.flojo.jam.game.creature.CreatureCore;
import de.flojo.jam.game.creature.CreatureId;
import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.flojo.jam.game.creature.skills.SkillMultiPunch;
import de.flojo.jam.game.creature.skills.SkillSimplePunch;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;

import java.util.LinkedHashSet;
import java.util.Set;

public class CreatureOger extends Creature {

    public CreatureOger(String name, Tile startBase, PlayerId playerId, boolean isOur, CreatureCollection cCollection, TrapCollection tCollection, IRenderData normal, IRenderData dying) {
        super(CreatureId.OGER, name, cCollection, tCollection, new CreatureBase(startBase), createOgerCore(playerId, normal, dying, isOur));
    }

    private static CreatureCore createOgerCore(PlayerId playerId, IRenderData normal, IRenderData dying, boolean isOur) {
        return new CreatureCore(playerId, isOur, normal, dying, createOgerAttributes());
    }

    private static CreatureAttributes createOgerAttributes() {
        Set<AbstractSkill> skills = new LinkedHashSet<>();
        skills.add(new SkillMultiPunch(4, 0, 2, "Grand Slam", "Wenn man sich einfach fallen lässt.", 2));
        // TODO: arrows
        return new CreatureAttributes(3, 3, skills);
    }
}

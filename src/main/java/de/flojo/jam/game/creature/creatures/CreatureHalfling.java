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

public class CreatureHalfling extends Creature {

    public CreatureHalfling(String name, Tile startBase, PlayerId playerId, boolean isOur, CreatureCollection cCollection, TrapCollection tCollection, IRenderData normal, IRenderData dying) {
        super(CreatureId.HALFLING, name, cCollection, tCollection, new CreatureBase(startBase), createHalflingCore(playerId, normal, dying, isOur));
    }

    private static CreatureCore createHalflingCore(PlayerId playerId, IRenderData normal, IRenderData dying, boolean isOur) {
        return new CreatureCore(playerId, isOur, normal, dying, createHalflingAttributes());
    }

    private static CreatureAttributes createHalflingAttributes() {
        Set<AbstractSkill> skills = new LinkedHashSet<>();
        skills.add(new SkillSimplePunch(1, 0, 2, "Einfacher Ellenstubser", "Ein einfacher Stubser mit dem Ellenbogen."));
        skills.add(new SkillMultiPunch(2, 0, 1, "Grand Slam", "Wenn man sich einfach fallen lässt.", 2));
        return new CreatureAttributes(3, 2, skills);
    }

}

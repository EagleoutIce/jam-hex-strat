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
import de.flojo.jam.game.creature.skills.SkillSimplePunch;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;

import java.util.LinkedHashSet;
import java.util.Set;

public class CreatureLizard extends Creature {

    public CreatureLizard(String name, Tile startBase, PlayerId playerId, boolean isOur, CreatureCollection cCollection,
                          TrapCollection tCollection, IRenderData normal, IRenderData dying) {
        super(CreatureId.LIZARD, name, cCollection, tCollection, new CreatureBase(startBase),
              createLizardCore(playerId, normal, dying, isOur));
    }

    private static CreatureCore createLizardCore(PlayerId playerId, IRenderData normal, IRenderData dying,
                                                 boolean isOur) {
        return new CreatureCore(playerId, isOur, normal, dying, createLizardAttributes());
    }

    private static CreatureAttributes createLizardAttributes() {
        Set<AbstractSkill> skills = new LinkedHashSet<>();
        skills.add(new SkillSimplePunch(2, 0, 2, "Push (2)", "Lizard-kick."));
        return new CreatureAttributes(CreatureId.LIZARD, skills);
    }
}

package de.flojo.jam.game.creature.creatures;

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

import java.util.LinkedHashSet;
import java.util.Set;

public class CreatureElf extends Creature {

    public CreatureElf(String name, Tile startBase, PlayerId playerId, boolean isOur, CreatureCollection cCollection, TrapCollection tCollection, IRenderData normal, IRenderData dying) {
        super(CreatureId.ELF, name, cCollection, tCollection, new CreatureBase(startBase), createElfCore(playerId, normal, dying, isOur));
    }

    private static CreatureCore createElfCore(PlayerId playerId, IRenderData normal, IRenderData dying, boolean isOur) {
        return new CreatureCore(playerId, isOur, normal, dying, createElfAttributes());
    }

    private static CreatureAttributes createElfAttributes() {
        Set<ICreatureSkill> skills = new LinkedHashSet<>();
        skills.add(new SimplePunch(2, 1, 3, "Ein mega Schuss", "Ein einfacher Schuss mit dem Elfenbogen."));
        // TODO: arrows
        return new CreatureAttributes(4, 1, skills);
    }
}

package de.flojo.jam.game.creature.creatures;

import java.util.HashSet;
import java.util.Set;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.CreatureAttributes;
import de.flojo.jam.game.creature.CreatureBase;
import de.flojo.jam.game.creature.CreatureCore;
import de.flojo.jam.game.creature.skills.ICreatureSkill;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;

public class CreaturePeasant extends Creature {


    public CreaturePeasant(Tile startBase, PlayerId playerId, IRenderData normal, IRenderData dying) {
        super("Bauer", new CreatureBase(startBase), createPeasantCore(playerId, normal, dying));
    }


    private static CreatureCore createPeasantCore(PlayerId playerId, IRenderData normal, IRenderData dying) {
        return new CreatureCore(playerId, normal, dying, createPeasantAttributes());
    }
    
    private static CreatureAttributes createPeasantAttributes() {
        Set<ICreatureSkill> skills = new HashSet<>();
        // skills.add(arg0);
        return new CreatureAttributes(1, 1, skills);
    }



}

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
import de.flojo.jam.game.creature.skills.ICreatureSkill;
import de.flojo.jam.game.creature.skills.SimplePunch;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;

public class CreaturePeasant extends Creature {

    public CreaturePeasant(String name, Tile startBase, PlayerId playerId, CreatureCollection cCollection, TrapCollection tCollection, IRenderData normal, IRenderData dying) {
        super(name, cCollection, tCollection, new CreatureBase(startBase), createPeasantCore(playerId, normal, dying));
    }

    private static CreatureCore createPeasantCore(PlayerId playerId, IRenderData normal, IRenderData dying) {
        return new CreatureCore(playerId, normal, dying, createPeasantAttributes());
    }
    
    private static CreatureAttributes createPeasantAttributes() {
        Set<ICreatureSkill> skills = new HashSet<>();
        skills.add(new SimplePunch(3, 1, 1, "Einfacher Harkenhieb", "Ein einfacher Hieb mit der Harke, der den Gegner bis zu drei Felder näher ans Jenseits befördert."));
        return new CreatureAttributes(1, 1, skills);
    }



}

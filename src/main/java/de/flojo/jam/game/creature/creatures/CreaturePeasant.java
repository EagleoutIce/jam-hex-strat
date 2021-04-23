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

public class CreaturePeasant extends Creature {

    public CreaturePeasant(String name, Tile startBase, PlayerId playerId, boolean isOur,
                           CreatureCollection cCollection, TrapCollection tCollection, IRenderData normal,
                           IRenderData dying) {
        super(CreatureId.PEASANT, name, cCollection, tCollection, new CreatureBase(startBase),
              createPeasantCore(playerId, normal, dying, isOur));
    }

    private static CreatureCore createPeasantCore(PlayerId playerId, IRenderData normal, IRenderData dying,
                                                  boolean isOur) {
        return new CreatureCore(playerId, isOur, normal, dying, createPeasantAttributes());
    }

    private static CreatureAttributes createPeasantAttributes() {
        Set<AbstractSkill> skills = new LinkedHashSet<>();
        skills.add(new SkillSimplePunch(3, 0, 1, "Punch (3)",
                                        "Ein starker Hieb mit der Harke, der den Gegner bis zu drei Felder näher ans Jenseits befördert."));
        skills.add(new SkillSimplePunch(2, 0, 1, "Punch (2)",
                                        "Ein normaler Hieb mit der Harke, der den Gegner bis zu zwei Felder näher ans Jenseits befördert."));
        skills.add(new SkillSimplePunch(1, 0, 2, "Punch (1)",
                                        "Ein weiter Hieb mit der Harke, der den Gegner ein Feld näher ans Jenseits befördert."));
        return new CreatureAttributes(4, 1, skills);
    }
}
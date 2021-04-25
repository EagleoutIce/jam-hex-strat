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
import de.flojo.jam.game.creature.skills.SkillToggleFly;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;

import java.util.LinkedHashSet;
import java.util.Set;

public class CreatureImp extends Creature {

    public CreatureImp(String name, Tile startBase, PlayerId playerId, boolean isOur, CreatureCollection cCollection,
                       TrapCollection tCollection, IRenderData normal, IRenderData dying) {
        super(CreatureId.IMP, name, cCollection, tCollection, new CreatureBase(startBase),
              createImpCore(playerId, normal, dying, isOur));
        // arrrrgh kill me
        getAttributes().getSkills().add(new SkillToggleFly(() -> isFlying() ? "Land" : "Fly", "Toggle Flystatus."));
    }

    private static CreatureCore createImpCore(PlayerId playerId, IRenderData normal, IRenderData dying, boolean isOur) {
        return new CreatureCore(playerId, isOur, normal, dying, createImpAttributes());
    }

    private static CreatureAttributes createImpAttributes() {
        final Set<AbstractSkill> skills = new LinkedHashSet<>();
        skills.add(new SkillSimplePunch(1, 0, 2, "Punch (1)", "Ein einfacher Stupser mit dem Ellenbogen."));
        skills.add(new SkillSimplePunch(5, 0, 1, "Punch (5)", "Eine Kopfnuss ins Jenseits."));
        return new CreatureAttributes(CreatureId.IMP, skills);
    }

}

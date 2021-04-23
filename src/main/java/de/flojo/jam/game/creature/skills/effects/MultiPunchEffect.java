package de.flojo.jam.game.creature.skills.effects;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.flojo.jam.game.creature.skills.CreatureSkillAOAGenerator;
import de.flojo.jam.game.creature.skills.IEffectTarget;
import de.flojo.jam.game.creature.skills.IProvideReadContext;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MultiPunchEffect implements IEffectTarget {

    private final IProvideReadContext context;
    private final int powerLeft;
    private final AbstractSkill skill;

    public MultiPunchEffect(IProvideReadContext context, int totalPower, AbstractSkill skill) {
        this.powerLeft = totalPower;
        this.context = context;
        this.skill = skill;
    }

    @Override
    public void effect(Creature target, Creature attacker) {
        Set<Tile> targetTiles = CreatureSkillAOAGenerator.getAOA(skill,
                                                                 context.getBoard().getTile(attacker.getCoordinate()),
                                                                 context.getBoard(), context.getCreatures());
        Set<Creature> allTargets = new HashSet<>();
        for (Tile targetTile : targetTiles) {
            Optional<Creature> mayCreature = context.getCreatures().get(targetTile.getCoordinate());
            mayCreature.ifPresent(allTargets::add);
        }
        allTargets.forEach(c ->
                                   new Thread(() -> new PunchEffect(context, powerLeft).effect(c, attacker)).start());
    }

}

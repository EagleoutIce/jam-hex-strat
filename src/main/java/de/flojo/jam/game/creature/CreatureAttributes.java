package de.flojo.jam.game.creature;

import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import de.flojo.jam.game.creature.skills.ICreatureSkill;
import de.flojo.jam.game.creature.skills.IProvideEffectContext;
import de.flojo.jam.game.creature.skills.SkillId;
import de.gurkenlabs.litiengine.Game;

public class CreatureAttributes {

    private final int maxMp;
    private final int maxAp;

    private final int currentMp;
    private final int currentAp;

    private final Set<ICreatureSkill> skills;

    public CreatureAttributes(int mp, int ap, Set<ICreatureSkill> skills) {
        this.maxMp = this.currentAp = ap;
        this.maxAp = this.currentMp = ap;
        this.skills = skills;
    }

    public int getMp() {
        return maxMp;
    }

    public int getAp() {
        return maxAp;
    }

    public Set<ICreatureSkill> getSkills() {
        return skills;
    }

    public Optional<ICreatureSkill> getSkill(SkillId wantedSkill) {
        for (ICreatureSkill skill : skills) {
            if (skill.getSkillId() == wantedSkill) {
                return Optional.of(skill);
            }
        }
        return Optional.empty();
    }

    public void useSkill(IProvideEffectContext context, SkillId wantedSkill, Creature attacker, Creature target) {
        getSkill(wantedSkill).ifPresentOrElse(s -> s.getEffect(context).effect(target, attacker), //
                () -> Game.log().log(Level.SEVERE, "Requested Skill {0} but not found.", wantedSkill));
    }

}

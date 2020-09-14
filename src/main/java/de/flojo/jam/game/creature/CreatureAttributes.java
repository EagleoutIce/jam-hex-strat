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

    private int currentMp;
    private int currentAp;

    private final Set<ICreatureSkill> skills;

    public CreatureAttributes(int mp, int ap, Set<ICreatureSkill> skills) {
        this.maxMp = this.currentMp = mp;
        this.maxAp = this.currentAp = ap;
        this.skills = skills;
    }

    public void reset() {
        this.currentAp = maxAp;
        this.currentMp = maxMp;
    }

    public boolean canDoSomething() {
        return currentAp != 0 || currentMp != 0;
    }

    
    public int getMpLeft() {
        return currentMp;
    }

    public int getApLeft() {
        return currentAp;
    }

    public boolean useAp() {
        if(this.currentAp <= 0)
            return false;
        this.currentAp -= 1;
        return true;
    }

    public boolean useMp() {
        if(this.currentMp <= 0)
            return false;
        this.currentMp -= 1;
        return true;
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
        getSkill(wantedSkill).ifPresentOrElse(s -> useSkill(context, s, attacker, target), //
                () -> Game.log().log(Level.SEVERE, "Requested Skill {0} but not found.", wantedSkill));
    }

    public void useSkill(IProvideEffectContext context, ICreatureSkill skill, Creature attacker, Creature target) {
        skill.getEffect(context).effect(target, attacker);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CreatureAttributes [currentAp=").append(currentAp).append(", currentMp=").append(currentMp)
                .append(", maxAp=").append(maxAp).append(", maxMp=").append(maxMp).append(", skills=").append(skills)
                .append("]");
        return builder.toString();
    }

}

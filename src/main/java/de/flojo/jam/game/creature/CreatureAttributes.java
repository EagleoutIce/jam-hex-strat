package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.creature.skills.ICreatureSkill;
import de.flojo.jam.game.creature.skills.IProvideReadContext;
import de.flojo.jam.game.creature.skills.SkillId;
import de.flojo.jam.util.HexStartLogger;
import de.flojo.jam.util.IProvideContext;

import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

public class CreatureAttributes {

    private final int maxMp;
    private final int maxAp;
    private final Set<ICreatureSkill> skills;
    private int currentMp;
    private int currentAp;

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
        return currentAp > 0 || currentMp > 0;
    }


    public int getMpLeft() {
        return currentMp;
    }

    public int getApLeft() {
        return currentAp;
    }

    public boolean useAp() {
        return useAp(1);
    }

    public boolean useAp(int n) {
        if (this.currentAp <= 0)
            return false;
        this.currentAp -= n;
        return true;
    }

    public boolean useMp() {
        if (this.currentMp <= 0)
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

    public void useSkill(IProvideReadContext context, SkillId wantedSkill, Creature attacker, Creature target) {
        getSkill(wantedSkill).ifPresentOrElse(s -> useSkill(context, s, attacker, target), //
                () -> HexStartLogger.log().log(Level.SEVERE, "Requested Skill {0} but not found.", wantedSkill));
    }

    public void useSkill(IProvideReadContext context, ICreatureSkill skill, Creature attacker, Creature target) {
        skill.getEffect(context).effect(target, attacker);
    }

    public void useSkill(IProvideReadContext context, ICreatureSkill skill, Creature attacker, Tile target) {
        skill.getEffect(context).effect(target, attacker);
    }

    @Override
    public String toString() {
        return "CreatureAttributes [currentAp=" + currentAp + ", currentMp=" + currentMp +
                ", maxAp=" + maxAp + ", maxMp=" + maxMp + ", skills=" + skills +
                "]";
    }

    public void setUsed() {
        this.currentAp = 0;
        this.currentMp = 0;
    }

}

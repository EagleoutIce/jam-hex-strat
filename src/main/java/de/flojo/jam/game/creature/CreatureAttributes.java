package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.flojo.jam.game.creature.skills.IProvideReadContext;
import de.flojo.jam.game.creature.skills.JsonDataOfSkill;
import de.flojo.jam.util.HexStratLogger;

import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

public class CreatureAttributes {

    private final int maxMp;
    private final int maxAp;
    private final Set<AbstractSkill> skills;
    private int currentMp;
    private int currentAp;

    public CreatureAttributes(int mp, int ap, Set<AbstractSkill> skills) {
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

    public boolean useAp(int n) {
        if (this.currentAp < n) {
            HexStratLogger.log().log(Level.WARNING, "Capping AP usage req: {0}, pres: {1}",
                                     new Object[]{n, this.currentAp});
            this.currentAp = 0;
            return false;
        }
        this.currentAp -= n;
        return true;
    }


    public boolean useMp(int n) {
        if (this.currentMp < n) {
            HexStratLogger.log().log(Level.WARNING, "Capping MP usage req: {0}, pres: {1}",
                                     new Object[]{n, this.currentMp});
            this.currentMp = 0;
            return false;
        }
        this.currentMp -= n;
        return true;
    }

    public boolean useMp() {
        return useMp(1);
    }

    public Set<AbstractSkill> getSkills() {
        return skills;
    }

    public Optional<AbstractSkill> getSkill(JsonDataOfSkill wantedSkill) {
        for (AbstractSkill skill : skills) {
            if (wantedSkill.equals(skill)) {
                return Optional.of(skill);
            }
        }
        return Optional.empty();
    }

    public void useSkill(IProvideReadContext context, JsonDataOfSkill wantedSkill, Creature attacker, Creature target) {
        getSkill(wantedSkill).ifPresentOrElse(s -> useSkill(context, s, attacker, target), //
                                              () -> HexStratLogger.log().log(Level.SEVERE,
                                                                             "Requested Skill {0} but not found.",
                                                                             wantedSkill));
    }

    public void useSkill(IProvideReadContext context, AbstractSkill skill, Creature attacker, Creature target) {
        skill.getEffect(context).effect(target, attacker);
    }

    public void useSkill(IProvideReadContext context, AbstractSkill skill, Creature attacker, Tile target) {
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

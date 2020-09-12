package de.flojo.jam.game.creature;

import java.util.Set;

import de.flojo.jam.game.creature.skills.ICreatureSkill;

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

}

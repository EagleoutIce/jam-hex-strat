package de.flojo.jam.game.creature;

import java.util.Set;

import de.flojo.jam.game.creature.skills.ICreatureSkill;

public class CreatureAttributes {

    private final int mp;
    private final int ap;

    private final Set<ICreatureSkill> skills;

    public CreatureAttributes(int mp, int ap, Set<ICreatureSkill> skills) {
        this.mp = mp;
        this.ap = ap;
        this.skills = skills;
    }

    public int getMp() {
        return mp;
    }

    public int getAp() {
        return ap;
    }

    public Set<ICreatureSkill> getSkills() {
        return skills;
    }

}

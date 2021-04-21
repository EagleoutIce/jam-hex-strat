package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.PunchEffect;

public class SkillSimplePunch extends AbstractSkill {

    public SkillSimplePunch(final int maximumPunchLength, final int minRange, final int maxRange, final String name, final String description) {
        super(maximumPunchLength, minRange, maxRange, 1, name, description, SkillId.SIMPLE_PUNCH);
    }

    @Override
    public int getRadius() {
        return 1; // aoe field
    }

    // area of attack
    @Override
    public CreatureSkillAOA getAOA() {
        return CreatureSkillAOA.LINE;
    }

    @Override
    public boolean doesFriendlyFire() {
        return true;
    }

    @Override
    public IEffectTarget getEffect(final IProvideReadContext context) {
        return new PunchEffect(context, maximumEffectLength);
    }

    @Override
    public boolean isRanged() {
        return false;// TODO: for archer
    }

    @Override
    public TargetOfSkill getTarget() {
        return TargetOfSkill.CREATURE;
    }

    @Override
    public String toString() {
        return "SimplePunch{" +
                "maximumPunchLength=" + maximumEffectLength +
                ", name='" + getNameWithFallback() + '\'' +
                ", description='" + description + '\'' +
                ", minRange=" + minRange +
                ", maxRange=" + maxRange +
                '}';
    }
}

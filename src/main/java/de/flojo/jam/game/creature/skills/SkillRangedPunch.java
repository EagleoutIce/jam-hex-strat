package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.PunchEffect;

public class SkillRangedPunch extends AbstractSkill {

    public SkillRangedPunch(final int maximumPunchLength, final int minRange, final int maxRange, final String name,
                            final String description) {
        this(maximumPunchLength, minRange, maxRange, name, description, 1);
    }

    public SkillRangedPunch(final int maximumPunchLength, final int minRange, final int maxRange, final String name,
                            final String description, final int cost) {
        super(maximumPunchLength, minRange, maxRange, 1, name, description, SkillId.RANGED_PUNCH, cost);
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
        return true;
    }

    @Override
    public TargetOfSkill getTarget() {
        return TargetOfSkill.CREATURE;
    }

}

package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.MultiPunchEffect;


public class SkillMultiPunch extends AbstractSkill {

    public SkillMultiPunch(final int maximumPunchLength, final int minRange, final int maxRange, final String name,
                           final String description, final int cost) {
        super(maximumPunchLength, minRange, maxRange, 0, name, description, SkillId.MULTI_PUNCH, cost);
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
        return new MultiPunchEffect(context, maximumEffectLength, this);
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public TargetOfSkill getTarget() {
        return TargetOfSkill.CREATURE;
    }

}

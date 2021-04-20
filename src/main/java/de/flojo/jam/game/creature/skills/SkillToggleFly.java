package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.ToggleFlyEffect;

import java.util.function.Supplier;

public class SkillToggleFly extends AbstractSkill {

    public SkillToggleFly(Supplier<String> nameSupplier, final String description) {
        super(1, ICreatureSkill.RANGE_SELF, ICreatureSkill.RANGE_SELF, 0, nameSupplier, description, SkillId.TOGGLE_FLY);
    }

    @Override
    public int getMinRange() {
        return minRange;
    }

    @Override
    public int getMaxRange() {
        return maxRange;
    }

    @Override
    public int getRadius() {
        return 1; // aoe field
    }

    // area of attack
    @Override
    public CreatureSkillAOA getAOA() {
        return CreatureSkillAOA.SINGLE;
    }

    @Override
    public int getCost() {
        return getSkillId().getCost();
    }

    @Override
    public boolean doesFriendlyFire() {
        return true;
    }

    @Override
    public IEffectTarget getEffect(final IProvideReadContext context) {
        return new ToggleFlyEffect(context);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return getNameWithFallback();
    }

    @Override
    public SkillId getSkillId() {
        return skillId;
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public int bonusOnRaised() {
        return bonusOnRaised;
    }

    @Override
    public boolean castOnAir() {
        return true;
    }

    @Override
    public TargetOfSkill getTarget() {
        return TargetOfSkill.CREATURE;
    }
}

package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.ToggleFlyEffect;

import java.util.function.Supplier;

public class ToggleFly implements ICreatureSkill {

    private final Supplier<String> nameSupplier;
    private final String description;
    private final int minRange;
    private final int maxRange;

    public ToggleFly(Supplier<String> nameSupplier, final String description) {
        this.nameSupplier = nameSupplier;
        this.description = description;
        this.minRange = ICreatureSkill.RANGE_SELF;
        this.maxRange = ICreatureSkill.RANGE_SELF;
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
        return nameSupplier.get();
    }

    @Override
    public SkillId getSkillId() {
        return SkillId.TOGGLE_FLY;
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public int bonusOnRaised() {
        return 0;
    }

    @Override
    public boolean castOnAir() {
        return true;
    }

    @Override
    public SkillTarget getTarget() {
        return SkillTarget.CREATURE;
    }
}

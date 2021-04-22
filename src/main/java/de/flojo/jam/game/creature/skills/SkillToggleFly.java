package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.ToggleFlyEffect;

import java.util.function.Supplier;

public class SkillToggleFly extends AbstractSkill {

    public SkillToggleFly(Supplier<String> nameSupplier, final String description) {
        this(nameSupplier, description, 1);
    }

    public SkillToggleFly(Supplier<String> nameSupplier, final String description, final int cost) {
        super(1, ICreatureSkill.RANGE_SELF, ICreatureSkill.RANGE_SELF, 0, nameSupplier, description, SkillId.TOGGLE_FLY, cost);
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
    public boolean doesFriendlyFire() {
        return true;
    }

    @Override
    public IEffectTarget getEffect(final IProvideReadContext context) {
        return new ToggleFlyEffect(context);
    }

    @Override
    public boolean isRanged() {
        return false;
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

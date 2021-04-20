package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.PlantTrapEffect;

import java.util.function.Supplier;

public class SkillPlantTrap extends AbstractSkill {

    public SkillPlantTrap(Supplier<String> nameSupplier, int minRange, int maxRange, final String description) {
        super(1, minRange, maxRange, 0, nameSupplier, description, SkillId.PLANT_TRAP);
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
        return CreatureSkillAOA.LINE;
    }

    @Override
    public int getCost() {
        return getSkillId().getCost();
    }

    @Override
    public boolean doesFriendlyFire() {
        return false;
    }

    @Override
    public IEffectTarget getEffect(final IProvideReadContext context) {
        return new PlantTrapEffect(context);
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
    public TargetOfSkill getTarget() {
        return TargetOfSkill.TILE;
    }

    @Override
    public int bonusOnRaised() {
        return bonusOnRaised;
    }

}

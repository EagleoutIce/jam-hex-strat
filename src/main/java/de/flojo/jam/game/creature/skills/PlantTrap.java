package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.PlantTrapEffect;

import java.util.function.Supplier;

public class PlantTrap implements ICreatureSkill {

    private final Supplier<String> nameSupplier;
    private final String description;
    private final int minRange;
    private final int maxRange;

    public PlantTrap(Supplier<String> nameSupplier, int minRange, int maxRange, final String description) {
        this.nameSupplier = nameSupplier;
        this.description = description;
        this.minRange = minRange;
        this.maxRange = maxRange;
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
        return nameSupplier.get();
    }

    @Override
    public SkillId getSkillId() {
        return SkillId.PLANT_TRAP;
    }

    @Override
    public boolean isRanged() {
        return false;
    }

    @Override
    public SkillTarget getTarget() {
        return SkillTarget.TILE;
    }

    @Override
    public int bonusOnRaised() {
        return 0;
    }

}

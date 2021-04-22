package de.flojo.jam.game.creature.skills;

import de.flojo.jam.networking.share.util.IAmJson;

import java.util.function.Supplier;

public abstract class AbstractSkill extends JsonDataOfSkill implements IAmJson, ICreatureSkill {

    AbstractSkill(int maximumEffectLength, int minRange, int maxRange, int bonusOnRaised, Supplier<String> nameSupplier, String description, SkillId skillId, final int cost) {
        super(maximumEffectLength, minRange, maxRange, bonusOnRaised, nameSupplier, description, skillId, cost);
    }

    AbstractSkill(int maximumEffectLength, int minRange, int maxRange, int bonusOnRaised, String name, String description, SkillId skillId, final int cost) {
        super(maximumEffectLength, minRange, maxRange, bonusOnRaised, name, description, skillId, cost);
    }

    @Override
    public int getMaximumEffectLength() {
        return maximumEffectLength;
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
    public int bonusOnRaised() {
        return bonusOnRaised;
    }

    @Override
    public SkillId getSkillId() {
        return skillId;
    }

    @Override
    public String getName() {
        return getNameWithFallback();
    }

    @Override
    public String getDescription() {
        return description;
    }

}

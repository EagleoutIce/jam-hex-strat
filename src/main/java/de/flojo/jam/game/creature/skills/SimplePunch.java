package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.PunchEffect;

public class SimplePunch implements ICreatureSkill {

    private final int maximumPunchLength;
    private final String name;
    private final String description;
    private final int minRange;
    private final int maxRange;

    public SimplePunch(final int maximumPunchLength, final int minRange, final int maxRange, final String name, final String description) {
        this.maximumPunchLength = maximumPunchLength;
        this.name = name;
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
        return 1;
    }

    @Override
    public boolean doesFriendlyFire() {
        return true;
    }

    @Override
    public IEffectCreature getEffect(final IProvideEffectContext context) {
        return new PunchEffect(context, maximumPunchLength);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SkillId getSkillId() {
        return SkillId.SIMPLE_PUNCH;
    }

}

package de.flojo.jam.game.creature.skills;

public interface ICreatureSkill {

    int RANGE_SELF = 0;

    int getMinRange();

    int getMaxRange();

    int getRadius();

    CreatureSkillAOA getAOA();

    int getCost();

    boolean doesFriendlyFire();

    // called for every target
    IEffectTarget getEffect(final IProvideReadContext context);

    String getDescription();

    String getName();

    SkillId getSkillId();

    int bonusOnRaised();

    // will traverse line until hit
    boolean isRanged();

    default boolean castOnGround() {
        return true;
    }

    default boolean castOnAir() {
        return false;
    }

    TargetOfSkill getTarget();
}

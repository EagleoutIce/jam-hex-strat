package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.TeleportationEffect;

public class SkillTeleportation extends AbstractSkill {

    public SkillTeleportation(final int minRange, final int maxRange, final String name,
                              final String description) {
        this(minRange, maxRange, name, description, 3);
    }

    public SkillTeleportation(final int minRange, final int maxRange, final String name,
                              final String description, final int cost) {
        super(1, minRange, maxRange, 1, name, description, SkillId.TELEPORTATION, cost);
    }

    @Override
    public int getRadius() {
        return 1; // aoe field
    }

    // area of attack
    @Override
    public CreatureSkillAOA getAOA() {
        return CreatureSkillAOA.CIRCLE;
    }

    @Override
    public boolean doesFriendlyFire() {
        return true;
    }

    @Override
    public IEffectTarget getEffect(final IProvideReadContext context) {
        return new TeleportationEffect(context);
    }

    @Override
    public boolean isRanged() {
        return true;
    }

    @Override
    public TargetOfSkill getTarget() {
        return TargetOfSkill.TILE;
    }

}

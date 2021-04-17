package de.flojo.jam.game.creature.skills;

import de.flojo.jam.game.creature.skills.effects.ToggleFlyEffect;

public class ToggleFly implements ICreatureSkill {

	private final String name;
	private final String description;
	private final int minRange;
	private final int maxRange;

	public ToggleFly(final String name, final String description) {
		this.name = name;
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
		return 1;
	}

	@Override
	public boolean doesFriendlyFire() {
		return true;
	}

	@Override
	public IEffectCreature getEffect(final IProvideEffectContext context) {
		return new ToggleFlyEffect();
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
}

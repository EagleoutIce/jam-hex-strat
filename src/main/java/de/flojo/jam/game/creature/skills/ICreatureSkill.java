package de.flojo.jam.game.creature.skills;

public interface ICreatureSkill {

	public static final int RANGE_SELF=0;

	public int getMinRange();
	public int getMaxRange();

	public int getRadius();
	public CreatureSkillAOA getAOA();

	public int getCost();

	public boolean doesFriendlyFire();

	// called for every target
	public IEffectCreature getEffect(final IProvideEffectContext context);

	public String getDescription();

	public String getName();

	public SkillId getSkillId();

	public int bonusOnRaised();

	// will traverse line until hit
	public boolean isRanged();
}

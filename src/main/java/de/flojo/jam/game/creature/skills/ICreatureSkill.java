package de.flojo.jam.game.creature.skills;

public interface ICreatureSkill {
    
    public int getMinRange();
    public int getMaxRange();

    public int getRadius();
    public CreatureSkillAOE getAOE();

    public int getCost();

    public boolean doesFriendlyFire();    

    // called for every target
    public IEffectCreature getEffect(final IProvideEffectContext context);

    public String getDescription();

    public String getName();

    public SkillId getSkillId();

}

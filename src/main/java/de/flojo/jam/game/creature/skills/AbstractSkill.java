package de.flojo.jam.game.creature.skills;

import de.flojo.jam.networking.share.util.IAmJson;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractSkill extends JsonDataOfSkill implements IAmJson, ICreatureSkill {

    AbstractSkill(int maximumEffectLength, int minRange, int maxRange, int bonusOnRaised, Supplier<String> nameSupplier, String description, SkillId skillId) {
        super(maximumEffectLength, minRange, maxRange, bonusOnRaised, nameSupplier, description, skillId);
    }

    AbstractSkill(int maximumEffectLength, int minRange, int maxRange, int bonusOnRaised, String name, String description, SkillId skillId) {
        super(maximumEffectLength, minRange, maxRange, bonusOnRaised, name, description, skillId);
    }
}

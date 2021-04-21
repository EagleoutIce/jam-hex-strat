package de.flojo.jam.game.creature.skills;

import de.flojo.jam.networking.share.util.IAmJson;

import java.util.Objects;
import java.util.function.Supplier;

public class JsonDataOfSkill implements IAmJson {
    protected final int maximumEffectLength;
    protected final transient String description;
    protected final int minRange;
    protected final int maxRange;
    protected final SkillId skillId;
    protected final int bonusOnRaised;
    private final transient Supplier<String> nameSupplier;
    private String name = null;

    public JsonDataOfSkill(final int maximumEffectLength, final int minRange, final int maxRange, final int bonusOnRaised, final Supplier<String> nameSupplier, final String description, SkillId skillId) {
        this.maximumEffectLength = maximumEffectLength;
        this.nameSupplier = nameSupplier;
        this.name = getNameWithFallback();
        this.description = description;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.skillId = skillId;
        this.bonusOnRaised = bonusOnRaised;
    }

    public JsonDataOfSkill(final int maximumEffectLength, final int minRange, final int maxRange, final int bonusOnRaised, final String name, final String description, SkillId skillId) {
        this(maximumEffectLength, minRange, maxRange, bonusOnRaised, () -> name, description, skillId);
    }

    protected String getNameWithFallback() {
        return nameSupplier == null ? name : nameSupplier.get();
    }

    @Override
    public String toJson() {
        this.name = getNameWithFallback();
        return IAmJson.super.toJson();
    }

    @Override
    public String toString() {
        return "SkillData{" +
                "maximumEffectLength=" + maximumEffectLength +
                ", name='" + getNameWithFallback() + '\'' +
                ", description='" + description + '\'' +
                ", minRange=" + minRange +
                ", maxRange=" + maxRange +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonDataOfSkill)) return false;
        JsonDataOfSkill that = (JsonDataOfSkill) o;
        return maximumEffectLength == that.maximumEffectLength && minRange == that.minRange && maxRange == that.maxRange && bonusOnRaised == that.bonusOnRaised && skillId == that.skillId;
    }
    // && Objects.equals(nameSupplier.get(), that.nameSupplier.get());

    @Override
    public int hashCode() {
        return Objects.hash(maximumEffectLength, minRange, maxRange, skillId, bonusOnRaised);
    }

}

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
    private final int cost;
    private String name;

    public JsonDataOfSkill(final int maximumEffectLength, final int minRange, final int maxRange,
                           final int bonusOnRaised, final Supplier<String> nameSupplier, final String description,
                           final SkillId skillId, final int cost) {
        this.maximumEffectLength = maximumEffectLength;
        this.nameSupplier = nameSupplier;
        this.name = getNameWithFallback();
        this.description = description;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.skillId = skillId;
        this.bonusOnRaised = bonusOnRaised;
        this.cost = cost;
    }

    public JsonDataOfSkill(final int maximumEffectLength, final int minRange, final int maxRange,
                           final int bonusOnRaised, final String name, final String description, final SkillId skillId,
                           final int cost) {
        this(maximumEffectLength, minRange, maxRange, bonusOnRaised, () -> name, description, skillId, cost);
    }

    protected String getNameWithFallback() {
        return nameSupplier == null ? name : nameSupplier.get();
    }

    public Supplier<String> getNameSupplierWithFallback() {
        return nameSupplier == null ? () -> name : nameSupplier;
    }

    @Override
    public String toJson() {
        this.name = getNameWithFallback();
        return IAmJson.super.toJson();
    }


    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "JsonDataOfSkill{" +
                "maximumEffectLength=" + maximumEffectLength +
                ", description='" + description + '\'' +
                ", minRange=" + minRange +
                ", maxRange=" + maxRange +
                ", skillId=" + skillId +
                ", bonusOnRaised=" + bonusOnRaised +
                ", nameSupplier=" + nameSupplier +
                ", cost=" + cost +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonDataOfSkill)) return false;
        JsonDataOfSkill that = (JsonDataOfSkill) o;
        return maximumEffectLength == that.maximumEffectLength && minRange == that.minRange && maxRange == that.maxRange && bonusOnRaised == that.bonusOnRaised && getCost() == that.getCost() && skillId == that.skillId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maximumEffectLength, minRange, maxRange, skillId, bonusOnRaised, getCost());
    }
}

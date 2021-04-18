package de.flojo.jam.game.creature.skills;

public enum SkillId {
    SIMPLE_PUNCH(1),
    TOGGLE_FLY(1);

    private final int cost;

    SkillId(final int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }
}

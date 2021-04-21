package de.flojo.jam.game.creature.skills;

public enum SkillId {
    SIMPLE_PUNCH(1, "simple_punch"),
    TOGGLE_FLY(1, "toggle_fly"),
    PLANT_TRAP(1, "plant_trap");

    private final int cost;
    private final String imgBaseName;

    SkillId(final int cost, String imgBaseName) {
        this.cost = cost;
        this.imgBaseName = imgBaseName;
    }

    public int getCost() {
        return cost;
    }

    public String getImgBaseName() {
        return imgBaseName;
    }
}

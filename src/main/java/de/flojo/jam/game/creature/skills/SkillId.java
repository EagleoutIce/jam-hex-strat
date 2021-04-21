package de.flojo.jam.game.creature.skills;

public enum SkillId {
    SIMPLE_PUNCH(1, "simple_punch", false),
    TOGGLE_FLY(1, "toggle_fly", true),
    PLANT_TRAP(1, "plant_trap", false);

    private final int cost;
    private final String imgBaseName;
    private final boolean isToggle;

    SkillId(final int cost, String imgBaseName, boolean isToggle) {
        this.cost = cost;
        this.imgBaseName = imgBaseName;
        this.isToggle = isToggle;
    }

    public int getCost() {
        return cost;
    }

    public String getImgBaseName() {
        return imgBaseName;
    }

    public boolean isToggle() {
        return isToggle;
    }
}

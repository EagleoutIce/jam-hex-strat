package de.flojo.jam.game.creature.skills;

public enum SkillId {
    SIMPLE_PUNCH("simple_punch", false),
    RANGED_PUNCH("ranged_punch", false),
    MULTI_PUNCH("multi_punch", false),
    TOGGLE_FLY("toggle_fly", true),
    PLANT_TRAP("plant_trap", false);

    private final String imgBaseName;
    private final boolean isToggle;

    SkillId(String imgBaseName, boolean isToggle) {
        this.imgBaseName = imgBaseName;
        this.isToggle = isToggle;
    }

    public String getImgBaseName() {
        return imgBaseName;
    }

    public boolean isToggle() {
        return isToggle;
    }
}

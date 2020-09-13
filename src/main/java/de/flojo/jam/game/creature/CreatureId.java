package de.flojo.jam.game.creature;

public enum CreatureId {
    // do not map directly here in case of multiple summons
    NONE("Nichts"),
    PEASANT("Bauer"),
    GOBLIN("Kobold");
    
    private final String name;

    CreatureId(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

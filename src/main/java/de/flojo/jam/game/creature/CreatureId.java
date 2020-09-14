package de.flojo.jam.game.creature;

public enum CreatureId {
    // do not map directly here in case of multiple summons
    NONE("Nichts", 0),
    PEASANT("Bauer", 6),
    GOBLIN("Kobold", 8),
    ELF("Elf", 10),
    HALFLING("Halbling", 8);
    
    private final String name;
    private final int cost;

    CreatureId(final String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    /**
     * @return the cost
     */
    public int getCost() {
        return cost;
    }
}

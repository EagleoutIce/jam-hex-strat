package de.flojo.jam.game.creature;

import java.awt.Graphics2D;
import java.util.Optional;
import java.util.UUID;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.creature.creatures.CreaturePeasant;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.SimpleImageRenderer;
import de.gurkenlabs.litiengine.graphics.IRenderable;

public class CreatureFactory implements IRenderable {
    private static final IRenderData PEASANT_P1_NORMAL = new SimpleImageRenderer("creatures/bauer_blau.png", -93d / 2.9,
            -99 / 1.27d);
    private static final IRenderData PEASANT_P2_NORMAL = new SimpleImageRenderer("creatures/bauer_lila.png",
            -93d / 1.53, -99 / 1.27d);

    private CreatureCollection creatures;

    public CreatureFactory() {
        creatures = new CreatureCollection();
    }

    public Creature summonPeasant(Tile startBase, PlayerId pId) {
        return summonPeasant(pId + "_BAUER_" + UUID.randomUUID(), startBase, pId);
    }

    public Creature summonPeasant(String uniqueName, Tile startBase, PlayerId pId) {
        return new CreaturePeasant(uniqueName, startBase, pId, creatures,
                pId.ifOne(PEASANT_P1_NORMAL, PEASANT_P2_NORMAL), pId.ifOne(PEASANT_P1_NORMAL, PEASANT_P2_NORMAL));
    }

    public CreatureCollection getCreatures() {
        return creatures;
    }

    public Optional<Creature> get(BoardCoordinate coordinate) {
        return creatures.get(coordinate);
    }

    public Creature get(String name) {
        return creatures.get(name);
    }

    public boolean isEmpty() {
        return creatures.isEmpty();
    }

    public int size() {
        return creatures.size();
    }

    public Creature get(int i) {
        return creatures.get(i);
    }

    @Override
    public void render(Graphics2D g) {
        creatures.render(g);
    }

    

}

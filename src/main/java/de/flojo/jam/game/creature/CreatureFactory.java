package de.flojo.jam.game.creature;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.creature.creatures.CreaturePeasant;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.SimpleImageRenderer;

public class CreatureFactory {
    private static final IRenderData PEASANT_P1_NORMAL = new SimpleImageRenderer("creatures/bauer_blau.png", -93d / 2.9,
            -99 / 1.27d);
    private static final IRenderData PEASANT_P2_NORMAL = new SimpleImageRenderer("creatures/bauer_lila.png", -93d / 1.53,
            -99 / 1.27d);

    public CreatureFactory() {
        // TODO: protype based factory?
    }

    public Creature summonPeasant(Tile startBase, PlayerId pId) {
        return new CreaturePeasant(startBase, pId, pId.ifOne(PEASANT_P1_NORMAL, PEASANT_P2_NORMAL), pId.ifOne(PEASANT_P1_NORMAL, PEASANT_P2_NORMAL));
    }

}

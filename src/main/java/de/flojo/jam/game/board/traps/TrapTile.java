package de.flojo.jam.game.board.traps;

import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.graphics.renderer.SimpleImageRenderer;

public enum TrapTile {
    SPIKE("Stachelfalle", new SimpleImageRenderer("tiles/traps/stachelfalle.png", -69 / 2d, -43 / 1.5),
            new SimpleImageRenderer("tiles/traps/stachelfalle_aktiv.png", -59 / 2d, -50 / 1.5),
            new TrapImprintNodeMap(TrapIdConstants.T_SPIKE, 0, 0));

    private final String displayName;
    private final IRenderData normalRenderer;
    private final IRenderData triggeredRenderer;
    private final TrapImprintNodeMap nodeMap;

    TrapTile(final String displayName, final IRenderData normalRenderer, final IRenderData triggeredRenderer,
    TrapImprintNodeMap nodeMap) {
        this.displayName = displayName;
        this.normalRenderer = normalRenderer;
        this.triggeredRenderer = triggeredRenderer;
        this.nodeMap = nodeMap;
    }

 
    public String getDisplayName() {
        return displayName;
    }

    public IRenderData getNormalRenderer() {
        return normalRenderer;
    }

    public IRenderData getTriggeredRenderer() {
        return triggeredRenderer;
    }

    public TrapImprintNodeMap getNodeMap() {
        return nodeMap;
    }
}

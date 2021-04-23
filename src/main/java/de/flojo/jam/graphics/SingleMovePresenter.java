package de.flojo.jam.graphics;

import de.flojo.jam.Main;
import de.flojo.jam.game.creature.Creature;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.image.BufferedImage;

public class SingleMovePresenter implements ISingleActionPresenter {

    private static final BufferedImage[] MOVES = {
            Resources.images().get("skills/move/move_0.png"),
            Resources.images().get("skills/move/move_1.png"),
            Resources.images().get("skills/move/move_2.png"),
            Resources.images().get("skills/move/move_3.png"),
            Resources.images().get("skills/move/move_4.png"),
            Resources.images().get("skills/move/move_5.png"),
            Resources.images().get("skills/move/move_6.png"),
            Resources.images().get("skills/move/move_7.png"),
            Resources.images().get("skills/move/move_8.png"),
            Resources.images().get("skills/move/move_9.png"),
            Resources.images().get("skills/move/move_more.png")
    };

    private static final ImageButton component = new ImageButton(107.5, 50, 0, 0, Resources.images().get("skills/move/move_1.png"), "", Main.GUI_FONT_SMALL);

    public SingleMovePresenter(Creature c) {
        update(c);
    }

    @Override
    public void update(Creature c) {
        final int left = c.getAttributes().getMpLeft();
        if (left >= 10) component.updateImage(MOVES[10]);
        else if (left >= 0) component.updateImage(MOVES[left]);
        component.setEnabled(left > 0);
    }

    @Override
    public GuiComponent get() {
        return component;
    }

    @Override
    public boolean hasImage() {
        return true;
    }
}

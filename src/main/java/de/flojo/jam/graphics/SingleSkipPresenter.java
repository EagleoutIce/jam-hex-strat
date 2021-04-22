package de.flojo.jam.graphics;

import de.flojo.jam.Main;
import de.flojo.jam.game.creature.Creature;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.resources.Resources;

public class SingleSkipPresenter implements ISingleActionPresenter {

    private static final GuiComponent component = new ImageButton(107.5, 50, 0, 0, Resources.images().get("skills/skip/skip.png"), "", Main.GUI_FONT_SMALL);

    @Override
    public void update(Creature c) {

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

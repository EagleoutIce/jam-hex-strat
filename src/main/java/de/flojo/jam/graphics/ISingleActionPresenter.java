package de.flojo.jam.graphics;

import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.gurkenlabs.litiengine.gui.GuiComponent;

public interface ISingleActionPresenter {

    char SPACE_CHAR = '_';

    static SingleSkillPresenter producePresenter(AbstractSkill skill) {
        return new SingleSkillPresenter(skill);
    }

    GuiComponent get();

    default boolean hasImage() {
        return false;
    }
}

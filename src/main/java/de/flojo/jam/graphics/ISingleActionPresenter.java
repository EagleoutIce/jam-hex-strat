package de.flojo.jam.graphics;

import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.flojo.jam.game.creature.skills.SkillId;
import de.gurkenlabs.litiengine.gui.GuiComponent;

public interface ISingleActionPresenter {

    char SPACE_CHAR = '_';

    static SingleSkillPresenter producePresenter(AbstractSkill skill, Creature creature) {
        if (skill.getSkillId().equals(SkillId.TOGGLE_FLY))
            return new SingleFlySkillPresenter(skill, creature);
        else return new SingleSkillPresenter(skill, creature);
    }

    default void update(Creature c) {}
    GuiComponent get();

    default boolean hasImage() {
        return false;
    }
}

package de.flojo.jam.graphics;

import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.image.BufferedImage;

public class SingleFlySkillPresenter extends SingleSkillPresenter {

    private static final BufferedImage LAND = Resources.images().get("skills/toggle_fly/land.png");

    public SingleFlySkillPresenter(AbstractSkill skill, Creature c) {
        super(skill, c);
    }

    @Override
    public String getPath(AbstractSkill skill, Creature c) {
        final String base = skill.getSkillId().getImgBaseName();
        return "skills/" + base + "/fly" + IMAGE_FILE_FORMAT;
    }

    public GuiComponent get() {
        return component;
    }

    @Override
    public void update(Creature c) {
        if (component instanceof ImageButton) {
            if (c.isFlying())
                ((ImageButton) component).updateImage(LAND);
            else
                ((ImageButton) component).updateImage(image);
        }
    }

    @Override
    public boolean hasImage() {
        return image != null;
    }

    public AbstractSkill getSkill() {
        return skill;
    }
}

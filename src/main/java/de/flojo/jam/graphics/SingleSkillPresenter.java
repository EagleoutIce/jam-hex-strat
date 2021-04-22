package de.flojo.jam.graphics;

import de.flojo.jam.Main;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.flojo.jam.util.HexStratLogger;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.resources.ResourceLoadException;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.image.BufferedImage;
import java.util.logging.Level;

public class SingleSkillPresenter implements ISingleActionPresenter {

    public static final String IMAGE_FILE_FORMAT = ".png";
    protected final AbstractSkill skill;
    protected final GuiComponent component;
    protected BufferedImage image;

    public SingleSkillPresenter(AbstractSkill skill, Creature c) {
        this.skill = skill;
        final String searchPath = getPath(skill, c);
        HexStratLogger.log().log(Level.INFO, "Searching Skill-Presenter Image: {0} (no force)", searchPath);
        try {
            image = Resources.images().get(searchPath);
        } catch (ResourceLoadException ignored) {
            image = null;
        }
        if (image == null) {
            // default: text Button
            component = new Button(skill.getName(), Main.GUI_FONT_SMALL);
        } else {
            component = new ImageButton(107.5, 50, 0, 0, image, "", Main.GUI_FONT_SMALL);
        }

        update(c);
    }

    public String getPath(AbstractSkill skill, Creature c) {
        final String base = skill.getSkillId().getImgBaseName();
        // TODO: use path
        return "skills/" + base + "/" + base + SPACE_CHAR + skill.getCost() +
                SPACE_CHAR + skill.getMaxRange() + SPACE_CHAR + skill.getMaximumEffectLength() + IMAGE_FILE_FORMAT;
    }

    public GuiComponent get() {
        return component;
    }

    public void update(Creature c) {
    }

    @Override
    public boolean hasImage() {
        return image != null;
    }

    public AbstractSkill getSkill() {
        return skill;
    }
}

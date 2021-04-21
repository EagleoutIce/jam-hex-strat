package de.flojo.jam.graphics;

import de.flojo.jam.Main;
import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.flojo.jam.util.HexStratLogger;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.resources.ResourceLoadException;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.image.BufferedImage;
import java.util.logging.Level;

public class SingleSkillPresenter implements ISingleActionPresenter {

    public static final String IMAGE_FILE_FORMAT = ".png";
    private final AbstractSkill skill;
    private final GuiComponent component;
    private BufferedImage image;

    public SingleSkillPresenter(AbstractSkill skill) {
        this.skill = skill;
        final String searchPath = getPath(skill);
        HexStratLogger.log().log(Level.FINE, "Searching Skill-Presenter Image: {0}", searchPath);
        try {
            image = Resources.images().get(searchPath);
        } catch (ResourceLoadException ignored) {
            image = null;
        }
        if (image == null) {
            // default: text Button
            component = new Button(skill.getName(), Main.GUI_FONT_SMALL);
        } else {
            component = new ImageButton(70, 70, 0, 0, image, "", Main.GUI_FONT_SMALL);
        }

    }

    public static String getPath(AbstractSkill skill) {
        final String base = skill.getSkillId().getImgBaseName();
        // TODO: use path
        return "skills/" + base + "/" + base + SPACE_CHAR + skill.getCost() +
                SPACE_CHAR + skill.getMaximumEffectLength() + IMAGE_FILE_FORMAT;
    }

    public GuiComponent get() {
        return component;
    }

    @Override
    public boolean hasImage() {
        return image != null;
    }

    public AbstractSkill getSkill() {
        return skill;
    }
}

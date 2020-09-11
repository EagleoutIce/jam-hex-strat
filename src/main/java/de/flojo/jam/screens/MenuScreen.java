package de.flojo.jam.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import de.flojo.jam.Main;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.ImageComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;

public class MenuScreen extends Screen {
    private static final BufferedImage background = Resources.images().get("painted03.jpeg");
    private ImageComponent buttonStartGame;

    public static final String NAME = "MENU";
    private boolean locked;

    public MenuScreen() {
        super(NAME);
    }

    @Override
    public void render(final Graphics2D g) {
        if (Game.world().environment() != null) {
            Game.world().environment().render(g);
        }

        ImageRenderer.render(g, background, 0, 0);

        // render info
        String info1 = "A Pain-Game";
        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT);
        TextRenderer.render(g, info1, Game.window().getWidth() - TextRenderer.getWidth(g, info1) - 25,
                7.0 + g.getFontMetrics().getHeight());

        String info2 = "This is an example menu";
        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT_SMALL);
        TextRenderer.render(g, info2, Game.window().getWidth() - TextRenderer.getWidth(g, info2) - 25,
                25.0 + 2 * g.getFontMetrics().getHeight());
        super.render(g);
    }

    @Override
    public void prepare() {
        super.prepare();

        Input.keyboard().onKeyPressed(KeyEvent.VK_ESCAPE, e -> {
            if (this.isVisible()) {
                System.exit(0);
            }
        });
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        double x = Game.window().getCenter().getX();
        double width = Game.window().getResolution().getWidth() / 3;
        double height = Game.window().getResolution().getHeight() / 6;
        this.buttonStartGame = new ImageComponent(x - width / 2.0, Game.window().getHeight() - height, width, height);
        this.buttonStartGame.setImage(null);
        this.buttonStartGame.setText("PLAY GAME");
        this.buttonStartGame.getAppearance().setForeColor(new Color(75, 160, 117));
        this.buttonStartGame.getAppearanceHovered().setForeColor(new Color(98, 198, 147));

        this.buttonStartGame.onClicked(e -> {
            if (this.locked) {
                return;
            }

            this.locked = true;
            this.buttonStartGame.setEnabled(false);
            Game.window().getRenderComponent().fadeOut(1000);
            Game.loop().perform(1500, () -> {
                displayIngameScreen();
                Game.window().getRenderComponent().fadeIn(1000);
                this.locked = false;
                this.buttonStartGame.setEnabled(true);
            });
        });

        this.getComponents().add(this.buttonStartGame);
    }

    private static void displayIngameScreen() {
        //
        Game.screens().display(IngameScreen.NAME);
    }

}

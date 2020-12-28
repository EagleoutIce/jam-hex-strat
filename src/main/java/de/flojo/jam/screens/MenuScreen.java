package de.flojo.jam.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import de.flojo.jam.Main;
import de.flojo.jam.graphics.Button;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;

public class MenuScreen extends Screen {
    private static final BufferedImage background = Resources.images().get("painted03.jpeg");
    private Button startGame;
    private Button showEditor;
    private Button showServer;

    public static final String NAME = "MENU";
    private boolean locked;

    public MenuScreen() {
        super(NAME);
        Game.log().info("Building Menu Screen");
    }

    @Override
    public void render(final Graphics2D g) {
        if (Game.world().environment() != null) {
            Game.world().environment().render(g);
        }

        ImageRenderer.render(g, background, 0, 0);

        // render info
        final String info1 = "Hex-Strat";
        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT);
        TextRenderer.render(g, info1, Game.window().getWidth() - TextRenderer.getWidth(g, info1) - 25,
                7.0 + g.getFontMetrics().getHeight());

        final String info2 = "A strategy game. With hex tiles";
        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT_SMALL);
        TextRenderer.render(g, info2, Game.window().getWidth() - TextRenderer.getWidth(g, info2) - 25,
                25.0 + 2 * g.getFontMetrics().getHeight());
        super.render(g);
    }

    @Override
    public void prepare() {
        super.prepare();
        Game.window().onResolutionChanged(r -> updatePositions());
        Game.loop().perform(100, this::updatePositions);
    }

    private void updatePositions() {
        final double x = Game.window().getCenter().getX();
        final double height = Game.window().getResolution().getHeight();
        this.startGame.setLocation(x - this.startGame.getWidth() / 2, height - this.startGame.getHeight() - 10);
        this.showEditor.setLocation(x - this.showEditor.getWidth() / 2d - 75d,
                height - this.startGame.getHeight() - this.showEditor.getHeight() - 20);
        this.showServer.setLocation(x - this.showServer.getWidth() / 2d + 75d,
                height - this.startGame.getHeight() - this.showServer.getHeight() - 20);
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        this.startGame = new Button("PLAY GAME", Main.GUI_FONT_SMALL);
        this.startGame.setColors(new Color(185, 45, 131), new Color(199, 104, 153));
        this.startGame.onClicked(e -> changeScreen(ConnectScreen.NAME, this.startGame));
        this.getComponents().add(this.startGame);

        this.showEditor = new Button("Editor", Main.GUI_FONT_SMALL.deriveFont(28f));
        this.showEditor.setColors(new Color(214, 65, 5), new Color(241, 138, 124));
        this.showEditor.onClicked(e -> changeScreen(EditorScreen.NAME, this.showEditor));
        this.getComponents().add(this.showEditor);

        this.showServer = new Button("Server", Main.GUI_FONT_SMALL.deriveFont(28f));
        this.showServer.setColors(new Color(214, 65, 5), new Color(241, 138, 124));
        this.showServer.onClicked(e -> changeScreen(ServerSetupScreen.NAME, this.showServer));
        this.getComponents().add(this.showServer);

        updatePositions();
    }

    private void changeScreen(final String name, final Button button) {
        if (this.locked || Objects.equals(Game.screens().current().getName(), name))
            return;

        Game.window().cursor().set(Main.DEFAULT_CURSOR);

        this.locked = true;
        button.setEnabled(false);
        Game.window().getRenderComponent().fadeOut(650);
        Game.loop().perform(950, () -> {
            Game.screens().display(name);
            Game.window().getRenderComponent().fadeIn(650);
            this.locked = false;
            button.setEnabled(true);
        });
    }

}

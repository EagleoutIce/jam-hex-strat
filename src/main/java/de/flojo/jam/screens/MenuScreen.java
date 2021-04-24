package de.flojo.jam.screens;

import de.flojo.jam.Main;
import de.flojo.jam.graphics.Button;
import de.flojo.jam.util.HexStratLogger;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class MenuScreen extends Screen {
    public static final String NAME = "MENU";
    public static final BufferedImage MAIN_BACKGROUND = Resources.images().get("main-background.jpg");
    private static final String HEX_STRAT = "Hex-Strat";
    private static final String HEX_STRAT_SUBTITLE = "A strategy game. With hex tiles";
    private Button startGame;
    private Button showEditor;
    private Button showServer;
    private boolean locked;

    public MenuScreen() {
        super(NAME);
        HexStratLogger.log().info("Building Menu Screen");
    }

    @Override
    public void render(final Graphics2D g) {
        ImageRenderer.render(g, MAIN_BACKGROUND, 0, 0);

        // render info
        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT);
        TextRenderer.render(g, HEX_STRAT, Game.window().getWidth() - TextRenderer.getWidth(g, HEX_STRAT) - 25,
                            7d + g.getFontMetrics().getHeight());

        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT_SMALL);
        TextRenderer.render(g, HEX_STRAT_SUBTITLE, Game.window().getWidth() - TextRenderer.getWidth(g,
                                                                                                    HEX_STRAT_SUBTITLE) - 25,
                            25d + 2 * g.getFontMetrics().getHeight());

        g.setColor(Color.WHITE);
        g.setFont(Main.TEXT_NORMAL);
        final var states = (Main.getStateOfServerLoad().isAlive() ? "Server: Loading; " : "") + (Main.getStateOfEditorLoad().isAlive() ? "Editor: Loading; " : "") + "Version: " + Main.getVersion();
        final var bounds = TextRenderer.getBounds(g, states);
        TextRenderer.render(g, states, Game.window().getWidth() - bounds.getWidth() - 25,
                            Game.window().getHeight() - bounds.getHeight() - 25);
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
        this.startGame.setColors(new Color(149, 161, 119), new Color(116, 125, 92));
        this.startGame.onClicked(e -> changeScreen(ConnectScreen.NAME, this.startGame));
        this.getComponents().add(this.startGame);

        this.showEditor = new Button("Editor", Main.GUI_FONT_SMALL.deriveFont(28f));
        this.showEditor.setColors(new Color(250, 232, 122), new Color(177, 172, 110));
        this.showEditor.onClicked(e -> changeScreen(EditorScreen.NAME, this.showEditor));
        this.getComponents().add(this.showEditor);

        this.showServer = new Button("Server", Main.GUI_FONT_SMALL.deriveFont(28f));
        this.showServer.setColors(new Color(250, 232, 122), new Color(177, 172, 110));
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
        Game.window().getRenderComponent().fadeOut(450);
        Game.loop().perform(450, () -> {
            Game.screens().display(name);
            Game.window().getRenderComponent().fadeIn(650);
            this.locked = false;
            button.setEnabled(true);
        });
    }
}

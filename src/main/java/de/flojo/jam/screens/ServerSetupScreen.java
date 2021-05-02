package de.flojo.jam.screens;

import de.flojo.jam.Main;
import de.flojo.jam.game.GameField;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.Button;
import de.flojo.jam.networking.server.ServerController;
import de.flojo.jam.util.FileHelper;
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.InputController;
import de.flojo.jam.util.ToolTip;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.gui.TextFieldComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;

import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class ServerSetupScreen extends Screen {
    public static final String NAME = "Server-Setup";
    private ServerController serverController;
    private TextFieldComponent portNumber;
    private TextFieldComponent startMoney;
    private Button startServer;
    private Button loadTerrain;
    private List<ToolTip<GuiComponent>> toolTips;

    private GameField gameField;

    private boolean serverStarted = false;
    private String chosenTerrainPath = null;

    private Button p1;
    private Button p2;
    private Button both;

    private boolean locked;

    public ServerSetupScreen() {
        super(NAME);
        HexStratLogger.log().info("Building server Screen");
    }

    private String serverStatus() {
        return serverController == null ? "Not started" : serverController.socketInfo();
    }


    private String playerStatus() {
        return serverController == null ? "" : serverController.playerInfo();
    }

    @Override
    public void render(final Graphics2D g) {
        if (Game.world().environment() != null) {
            Game.world().environment().render(g);
        }

        gameField.render(g);

        // render info
        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT);
        int largeHeight = g.getFontMetrics().getHeight();
        TextRenderer.render(g, "Server-Configuration", Main.INNER_MARGIN,
                            7.0 + largeHeight);

        g.setFont(Main.TEXT_STATUS);
        TextRenderer.render(g, "Server status: " + serverStatus(), Main.INNER_MARGIN,
                            15.0 + g.getFontMetrics().getHeight() + largeHeight);
        final var playerStatus = playerStatus();
        if (playerStatus != null)
            TextRenderer.render(g, "Players: " + playerStatus(), Main.INNER_MARGIN,
                                15.0 + 2 * g.getFontMetrics().getHeight() + largeHeight);
        TextRenderer.render(g, "Port: ", Main.INNER_MARGIN,
                            Game.window().getHeight() - 50d);
        TextRenderer.render(g, "Start Money: ", Main.INNER_MARGIN,
                            Game.window().getHeight() - 80d);
        super.render(g);
        List<ToolTip<GuiComponent>> presenterToolTips = gameField.getPresenter().getToolTips();
        if (presenterToolTips != null)
            presenterToolTips.forEach(t -> t.render(g));
        List<ToolTip<GuiComponent>> buildPhaseToolTips = gameField.getBuildingPhaseButtons().getToolTips();
        if (buildPhaseToolTips != null)
            buildPhaseToolTips.forEach(t -> t.render(g));
        if (this.toolTips != null)
            this.toolTips.forEach(t -> t.render(g));
    }


    @Override
    public void prepare() {
        super.prepare();
        Game.window().onResolutionChanged(r -> updatePositions());
        Game.loop().perform(100, this::updatePositions);
        gameField = new GameField(this, ServerSetupScreen.NAME, null);
        gameField.getBoard().resetZoom();
        InputController.get().onKeyPressed(KeyEvent.VK_ESCAPE, e -> {
            if (stopServer(true)) changeScreen(MenuScreen.NAME);
        }, ServerSetupScreen.NAME);
    }

    private void updatePositions() {
        final double height = Game.window().getResolution().getHeight();
        final double width = Game.window().getResolution().getWidth();
        this.portNumber.setLocation(Main.INNER_MARGIN + 60d, height - 45d);
        this.startMoney.setLocation(Main.INNER_MARGIN + 165d, height - 75d);
        this.startServer.setLocation(width - this.startServer.getWidth() - 0.5 * Main.INNER_MARGIN - 10d,
                                     height - this.startServer.getHeight() - 15);
        this.loadTerrain.setLocation(
                width - this.startServer.getWidth() - Main.INNER_MARGIN - this.loadTerrain.getWidth(),
                height - this.loadTerrain.getHeight() - 15);
        this.p1.setLocation(width - Main.INNER_MARGIN - p1.getWidth() - p2.getWidth() - both.getWidth() - 35d, 23d);
        this.p2.setLocation(width - Main.INNER_MARGIN - p1.getWidth() - both.getWidth() - 30d, 23d);
        this.both.setLocation(width - Main.INNER_MARGIN - both.getWidth() - 10d, 23d);
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();

        toolTips = new CopyOnWriteArrayList<>();
        this.startServer = new Button("Start", Main.GUI_FONT_SMALL);
        toolTips.add(new ToolTip<>(startServer,
                                   () -> startServer.getText() + " the Server\nPort: " + portNumber.getText() + "\nStart Money: " + startMoney.getText(),
                                   Color.gray));
        this.startServer.onClicked(c -> {
            if (serverStarted) {
                stopServer(true);
            } else {
                startServer();
            }
        });

        this.getComponents().add(startServer);

        this.loadTerrain = new Button("Terrain", Main.GUI_FONT_SMALL);
        toolTips.add(
                new ToolTip<>(loadTerrain, () -> "Load a terrain\nThis can be created with the editor.", Color.gray));
        // jeah jeah.. outsource da shit -.-
        this.loadTerrain.onClicked(c -> loadTerrain());
        this.getComponents().add(loadTerrain);

        // pos will be recalculated
        this.portNumber = new TextFieldComponent(0, 0, 100, 40, Integer.toString(Main.getHexStratConfiguration().getDefaultPort()));
        this.portNumber.setFormat("[0-9]{1,4}");
        this.getComponents().add(portNumber);
        this.startMoney = new TextFieldComponent(0, 0, 100, 40, Integer.toString(Main.getHexStratConfiguration().getDefaultStartMoney()));
        this.startMoney.setFormat("[0-9]{1,6}");
        this.getComponents().add(startMoney);
        initShowTeamButtons();
        updatePositions();
    }

    private void initShowTeamButtons() {
        p1 = new Button("P1", Main.GUI_FONT_SMALL);
        p1.onClicked(c -> {
            gameField.setPlayerId(PlayerId.ONE);
            p1.setColors(Color.GREEN, Color.GREEN.brighter());
            p2.setColors(Color.WHITE, Color.WHITE.darker());
            both.setColors(Color.WHITE, Color.WHITE.darker());
        });
        p2 = new Button("P2", Main.GUI_FONT_SMALL);
        p2.onClicked(c -> {
            gameField.setPlayerId(PlayerId.TWO);
            p1.setColors(Color.WHITE, Color.WHITE.darker());
            p2.setColors(Color.GREEN, Color.GREEN.brighter());
            both.setColors(Color.WHITE, Color.WHITE.darker());
        });
        both = new Button("Both", Main.GUI_FONT_SMALL);
        both.onClicked(c -> {//
            gameField.setPlayerId(null);
            p1.setColors(Color.WHITE, Color.WHITE.darker());
            p2.setColors(Color.WHITE, Color.WHITE.darker());
            both.setColors(Color.GREEN, Color.GREEN.brighter());
        });
        this.getComponents().add(p1);
        this.getComponents().add(p2);
        this.getComponents().add(both);
        both.setColors(Color.GREEN, Color.GREEN.brighter());
    }

    private void loadTerrain() {
        String chosen = FileHelper.askForTerrainPathLoad();
        if (chosen == null) {
            HexStratLogger.log().info("Load was cancelled.");
            return;
        }
        this.chosenTerrainPath = chosen;
        loadCurrentTerrain();
    }

    private void loadCurrentTerrain() {
        if (chosenTerrainPath == null)
            return;
        HexStratLogger.log().log(Level.INFO, "Loading from: \"{0}\"", chosenTerrainPath);
        try {
            final var map = new TerrainMap(GameField.BOARD_WIDTH, GameField.BOARD_HEIGHT,
                                           new FileInputStream(chosenTerrainPath),
                                           chosenTerrainPath);
            gameField.updateTerrain(map);
            HexStratLogger.log().log(Level.INFO, "Loaded Terrain: \"{0}\"", gameField.getTerrainName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        HexStratLogger.log().log(Level.INFO, "Starting Server on Port {0}", getAddress());
        this.startServer.setText("Stop");
        this.portNumber.setEnabled(false);
        this.startMoney.setEnabled(false);
        this.loadTerrain.setEnabled(false);
        serverController = new ServerController(getAddress(), gameField, this::onNetworkUpdate,
                                                Integer.parseInt(this.startMoney.getText()));
        serverController.start();
        serverStarted = true;
        updatePositions();
    }

    private InetSocketAddress getAddress() {
        return new InetSocketAddress(Integer.parseInt(this.portNumber.getText()));
    }

    private void onNetworkUpdate(String... data) {
        HexStratLogger.log().log(Level.INFO, "Got notified! ({0})", Arrays.toString(data));

        if (data.length == 0)
            return;

        if ("STOPPED".equals(data[0])) {
            stopServer(false);
        } else {
            HexStratLogger.log().log(Level.WARNING, "Unknown Data on first Element? ({0})", data[0]);
        }
    }

    private boolean askStupidUserForConfirmationOnExit() {
        final Object[] options = {"Yes", "No"};
        final int n = JOptionPane.showOptionDialog(Game.window().getRenderComponent(),
                                                   "Are you sure, that you want to stop the server?", null,
                                                   JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                                   options, options[1]);
        return n != 0;
    }

    // only true if stopped and reset
    private boolean stopServer(boolean ask) {
        if (ask && this.serverStarted && askStupidUserForConfirmationOnExit())
            return false;
        HexStratLogger.log().log(Level.INFO, "Stopping Server on Port {0}", getAddress());
        this.startServer.setText("Start");
        this.portNumber.setEnabled(true);
        this.startMoney.setEnabled(true);
        this.loadTerrain.setEnabled(true);
        if (serverController != null)
            this.serverController.stop();
        this.serverController = null;
        serverStarted = false;
        gameField.reset();
        loadCurrentTerrain();
        return true;
    }

    private void changeScreen(final String name) {
        if (this.locked)
            return;

        Game.window().cursor().set(Main.DEFAULT_CURSOR);

        this.locked = true;
        Game.window().getRenderComponent().fadeOut(450);
        Game.loop().perform(450, () -> {
            Game.screens().display(name);
            Game.window().getRenderComponent().fadeIn(650);
            this.locked = false;
        });
    }

}

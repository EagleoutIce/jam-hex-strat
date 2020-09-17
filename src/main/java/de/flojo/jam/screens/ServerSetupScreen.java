package de.flojo.jam.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.logging.Level;

import de.flojo.jam.Main;
import de.flojo.jam.game.GameField;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.Button;
import de.flojo.jam.networking.server.ServerController;
import de.flojo.jam.util.FileHelper;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.TextFieldComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;

public class ServerSetupScreen extends Screen {
    private ServerController serverController;
    public static final String NAME = "Server-Setup";

    public static final String DEFAULT_PORT = "1096";

    private TextFieldComponent portNumber;
    private Button startServer;
    private Button loadTerrain;

    private GameField gameField;

    private boolean serverStarted = false;
    private String chosenTerrainPath = null;

    private Button p1;
    private Button p2;
    private Button both;

    public ServerSetupScreen() {
        super(NAME);
        Game.log().info("Building server Screen");
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
        TextRenderer.render(g, "Players: " + playerStatus(), Main.INNER_MARGIN,
                15.0 + 2*g.getFontMetrics().getHeight() + largeHeight);
        TextRenderer.render(g, "Port: ", Main.INNER_MARGIN,
                    Game.window().getHeight() - 50d);

        super.render(g);
    }


    @Override
    public void prepare() {
        super.prepare();
        Game.window().onResolutionChanged(r -> updatePositions());
        Game.loop().perform(100, this::updatePositions);
        gameField = new GameField(this, ServerSetupScreen.NAME, null);
    }

    private void updatePositions() {
        final double height = Game.window().getResolution().getHeight();
        final double width = Game.window().getResolution().getWidth();
        this.portNumber.setLocation(Main.INNER_MARGIN + 60d, height - 47d);
        this.startServer.setLocation(width - this.startServer.getWidth() - 0.5*Main.INNER_MARGIN - 10d, height - this.startServer.getHeight());
        this.loadTerrain.setLocation(width - this.startServer.getWidth() - Main.INNER_MARGIN - this.loadTerrain.getWidth(), height - this.loadTerrain.getHeight());
        this.p1.setLocation(width - Main.INNER_MARGIN - p1.getWidth() - p2.getWidth() - both.getWidth() - 35d, 23d);
        this.p2.setLocation(width - Main.INNER_MARGIN - p1.getWidth() - both.getWidth() - 30d, 23d);
        this.both.setLocation(width - Main.INNER_MARGIN - both.getWidth() - 10d, 23d);
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();

        this.startServer = new Button("Start", Main.GUI_FONT_SMALL);
        this.startServer.onClicked(c -> {
            if(serverStarted) {
                stopServer();
            } else {
                startServer();
            }});

        this.getComponents().add(startServer);

        this.loadTerrain = new Button("Terrain", Main.GUI_FONT_SMALL);
        // jeah jeah.. outsource da shit -.-
        this.loadTerrain.onClicked(c -> loadTerrain() );
        this.getComponents().add(loadTerrain);

        // pos will be recalculated
        this.portNumber = new TextFieldComponent(0, 0, 100, 40, DEFAULT_PORT);
        this.portNumber.setFormat("[0-9]{1,4}");
        this.getComponents().add(portNumber);
        initShowTeamButtons();
        updatePositions();
    }

    private void initShowTeamButtons() {
        p1 = new Button("P1", Main.GUI_FONT_SMALL);
        p1.onClicked(c -> {//
            gameField.setPlayerId(PlayerId.ONE);
            p1.setColors(Color.GREEN, Color.GREEN.brighter());
            p2.setColors(Color.WHITE, Color.WHITE.darker());
            both.setColors(Color.WHITE, Color.WHITE.darker());
        });
        p2 = new Button("P2", Main.GUI_FONT_SMALL);
        p2.onClicked(c -> {//
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
            Game.log().info("Load was cancelled.");
            return;
        }

        this.chosenTerrainPath = chosen;

        loadCurrentTerrain();
    }

    private void loadCurrentTerrain() {
        if(chosenTerrainPath == null)
            return;
        Game.log().log(Level.INFO, "Loading from: \"{0}\"", chosenTerrainPath);
        try {
            TerrainMap map = new TerrainMap(GameField.BOARD_WIDTH, GameField.BOARD_HEIGHT, new FileInputStream(new File(chosenTerrainPath)),
            chosenTerrainPath);
            gameField.updateTerrain(map);
            Game.log().log(Level.INFO, "Loaded Terrain: \"{0}\"", gameField.getTerrainName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        Game.log().log(Level.INFO, "Starting Server on Port {0}", getAdress());
        this.startServer.setText("Stopp");
        this.portNumber.setEnabled(false);
        this.loadTerrain.setEnabled(false);
        serverController = new ServerController(getAdress(), gameField, this::onNetworkUpdate);
        serverController.start();
        serverStarted = true;
    }

    private InetSocketAddress getAdress() {
        return new InetSocketAddress(Integer.parseInt(this.portNumber.getText()));
    }

    private void onNetworkUpdate(String... data){
        Game.log().log(Level.INFO, "Got notified! ({0})", Arrays.toString(data));

        if(data.length == 0)
            return;

        switch(data[0]) {
            case "STOPPED":
                stopServer();
                break;

            default:
                Game.log().log(Level.WARNING, "Unknown Data on first Element? ({0})", data[0]);
        }
    }


    private void stopServer() {
        Game.log().log(Level.INFO, "Stopping Server on Port {0}", getAdress());
        this.startServer.setText("Start");
        this.portNumber.setEnabled(true);
        this.loadTerrain.setEnabled(true);
        if(serverController != null)
            this.serverController.stop();
        this.serverController = null;
        serverStarted = false;
        gameField.reset();
        loadCurrentTerrain();
    }

}

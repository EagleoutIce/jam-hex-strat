package de.flojo.jam.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.util.logging.Level;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.graphics.Button;
import de.flojo.jam.networking.server.ServerController;
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

    private Board board;

    private boolean serverStarted = false;
    


    public ServerSetupScreen() {
        super(NAME);
        Game.log().info("Building server Screen");
        updatePositions();
    }

    private String serverStatus() {
        return serverController == null ? "Not started" : serverController.socketInfo();
    }

    @Override
    public void render(final Graphics2D g) {
        if (Game.world().environment() != null) {
            Game.world().environment().render(g);
        }

        board.render(g);

        // render info
        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT);
        int largeHeight = g.getFontMetrics().getHeight();
        TextRenderer.render(g, "Server-Configuration", Main.INNER_MARGIN,
                7.0 + largeHeight);
    
        g.setFont(Main.TEXT_STATUS);
        TextRenderer.render(g, "Server status: " + serverStatus(), Main.INNER_MARGIN,
                15.0 + g.getFontMetrics().getHeight() + largeHeight);
        TextRenderer.render(g, "Port: ", Main.INNER_MARGIN,
                    Game.window().getHeight() - 50d);

        super.render(g);
    }

    @Override
    public void prepare() {
        super.prepare();
        Game.window().onResolutionChanged(r -> {
            updatePositions();
        });
        board = new Board(Main.BOARD_WIDTH, Main.BOARD_HEIGHT, Main.FIELD_BACKGROUND, "configs/empty.terrain");
    }

    private void updatePositions() {
        final double height = Game.window().getResolution().getHeight();
        final double width = Game.window().getResolution().getWidth();
        this.portNumber.setLocation(Main.INNER_MARGIN + 60d, height - 45d);
        this.startServer.setLocation(width - this.startServer.getWidth() - 0.5*Main.INNER_MARGIN, height - this.startServer.getHeight());
        this.loadTerrain.setLocation(width - this.startServer.getWidth() - Main.INNER_MARGIN - this.loadTerrain.getWidth(), height - this.loadTerrain.getHeight());
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();

        this.startServer = new Button("Start", Main.GUI_FONT_SMALL);
        this.startServer.onClicked(c -> {
            if(serverStarted) {
                this.startServer.setText("Start");
                stopServer();
                serverStarted = false;
            } else {
                this.startServer.setText("Stopp");
                startServer();
                serverStarted = true;
            }});
        this.getComponents().add(startServer);

        this.loadTerrain = new Button("Terrain", Main.GUI_FONT_SMALL);
        this.loadTerrain.onClicked(c -> {
            // jeah jeah.. outsource da shit -.-
            loadTerrain();
        });
        this.getComponents().add(loadTerrain);

        // pos will be recalculated
        this.portNumber = new TextFieldComponent(0, 0, 100, 40, DEFAULT_PORT);
        this.portNumber.setFormat("[0-9]{1,4}");
        this.getComponents().add(portNumber);
        updatePositions();
    }

    private void loadTerrain() {
        String chosen = EditorScreen.loadTerrain();
        if (chosen == null) {
            Game.log().info("Load was cancelled.");
            return;
        }

        Game.log().log(Level.INFO, "Loading from: \"{0}\"", chosen);
        try {
            TerrainMap map = new TerrainMap(Main.BOARD_WIDTH, Main.BOARD_HEIGHT, new FileInputStream(new File(chosen)),
                    chosen);
            this.board.setTerrainMap(map);
            Game.log().log(Level.INFO, "Loaded Terrain: \"{0}\"", board.getTerrainMap().getTerrain().getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        this.portNumber.setEnabled(false);
        this.loadTerrain.setEnabled(false);
        serverController = new ServerController(new InetSocketAddress(Integer.parseInt(this.portNumber.getText())), board);
        serverController.start();
    }


    private void stopServer() {
        this.portNumber.setEnabled(true);
        this.loadTerrain.setEnabled(true);
        this.serverController.stop();
        this.serverController = null;
    }

}

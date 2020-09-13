package de.flojo.jam.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;

import de.flojo.jam.Main;
import de.flojo.jam.graphics.Button;
import de.flojo.jam.networking.client.ClientController;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.TextFieldComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;

public class ConnectScreen extends Screen {
    private static final BufferedImage background = Resources.images().get("painted03.jpeg");

    private ClientController clientController;
    public static final String NAME = "Game-Connect";

    private TextFieldComponent portNumber;
    private TextFieldComponent adress;
    private Button connect;

    private boolean connected = false;

    public ConnectScreen() {
        super(NAME);
        Game.log().info("Building game connect Screen");
        updatePositions();
    }

    @Override
    public void render(final Graphics2D g) {
        if (Game.world().environment() != null) {
            Game.world().environment().render(g);
        }
        ImageRenderer.render(g, background, 0, 0);
        // render info
        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT);
        int largeHeight = g.getFontMetrics().getHeight();
        TextRenderer.render(g, "Connection Menu", Main.INNER_MARGIN, 7.0 + largeHeight);

        g.setFont(Main.TEXT_STATUS);
        TextRenderer.render(g, "Connection status: TODO:", Main.INNER_MARGIN,
                15.0 + g.getFontMetrics().getHeight() + largeHeight);
        TextRenderer.render(g, "Port: ", Main.INNER_MARGIN,
                (Game.window().getHeight() + portNumber.getHeight() + 200) / 2);

        TextRenderer.render(g, "Adr.: ", Main.INNER_MARGIN,
                (Game.window().getHeight() + portNumber.getHeight()) / 2);


        super.render(g);
    }

    @Override
    public void prepare() {
        super.prepare();
        Game.window().onResolutionChanged(r -> {
            updatePositions();
        });
    }

    private void updatePositions() {
        final double height = Game.window().getResolution().getHeight();
        final double width = Game.window().getResolution().getWidth();
        this.portNumber.setLocation(Main.INNER_MARGIN + 70d, (height + portNumber.getHeight() + 200) / 2 - 26);
        this.adress.setLocation(Main.INNER_MARGIN + 45d, (height + adress.getHeight()) / 2 - 26);
        this.connect.setLocation(width - this.connect.getWidth() - 0.5 * Main.INNER_MARGIN,
                height - this.connect.getHeight());
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();

        this.connect = new Button("Verbinde", Main.GUI_FONT_SMALL);
        this.connect.onClicked(c -> {
            if (connected)
                connected = false;
            else
                connected = connect();

            if (!connected)
                disconnect();
        });
        this.getComponents().add(connect);

        // pos will be recalculated
        this.portNumber = new TextFieldComponent(0, 0, 100, 70, ServerSetupScreen.DEFAULT_PORT);
        this.portNumber.setFormat("[0-9]{1,4}");
        this.getComponents().add(portNumber);
        this.adress = new TextFieldComponent(0, 0, 600, 70, "localhost");
        this.adress.setFormat("[a-zA-Z.0-9]{1,200}");
        this.getComponents().add(adress);
        updatePositions();
    }

    private boolean connect() {
        this.portNumber.setEnabled(false);
        this.portNumber.setEnabled(false);
        this.connect.setText("Trenne");
        try {
            clientController = new ClientController(new URI("ws://" + this.adress.getText() + ":" + this.portNumber.getText() ));
            return clientController.tryConnect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            // just to be sure
            clientController = null;
        }
        return false;
    }

    private void disconnect() {
        this.portNumber.setEnabled(true);
        this.connect.setText("Verbinde");
        this.clientController.close();
        this.clientController = null;
    }
}

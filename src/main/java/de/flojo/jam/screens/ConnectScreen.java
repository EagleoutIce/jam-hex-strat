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

    private static final String[] RANDOM_NAMES = new String[] {
        "PeterMeter", "Pain Gain", "H4ns3l", "Joseph", "Achtung Butter!", "Hallo Mami",
        "Jam-Ben", "Niemand", "Du", "Ich", "Wer?", "Name", "Bluhme", "Jonas", "Flo", "W3rW0lf"
    };

    private boolean locked;

    private static ClientController clientController;
    public static final String NAME = "Game-Connect";

    private static TextFieldComponent nameField;
    private TextFieldComponent portNumber;
    private TextFieldComponent adress;
    private Button connect;

    private boolean connected = false;

    private ConnectScreen() {
        super(NAME);
        Game.log().info("Building game connect Screen");
        updatePositions();
    }

    private static final ConnectScreen instance = new ConnectScreen();

    public static ConnectScreen get() {
        return instance;
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

        TextRenderer.render(g, "Name: ", Main.INNER_MARGIN,
                (Game.window().getHeight() + portNumber.getHeight() + 400) / 2);

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
        this.nameField.setLocation(Main.INNER_MARGIN + 55d, (height + nameField.getHeight() + 400) / 2 - 26);
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
            else {
                changeScreen(IngameScreen.NAME, this.connect);
            }
        });
        this.getComponents().add(connect);

        // pos will be recalculated
        this.portNumber = new TextFieldComponent(0, 0, 100, 70, ServerSetupScreen.DEFAULT_PORT);
        this.portNumber.setFormat("[0-9]{0,4}");
        this.getComponents().add(portNumber);
        this.adress = new TextFieldComponent(0, 0, 600, 70, "localhost");
        this.adress.setFormat("[a-zA-Z.0-9]{0,200}");
        this.getComponents().add(adress);
        nameField = new TextFieldComponent(0, 0, 600, 70, Game.random().choose(RANDOM_NAMES));
        nameField.setFormat(".{0,25}");
        this.getComponents().add(nameField);
        updatePositions();
    }

    private boolean connect() {
        this.portNumber.setEnabled(false);
        this.adress.setEnabled(false);
        nameField.setEnabled(false);
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
        this.adress.setEnabled(true);
        this.nameField.setEnabled(true);
        this.connect.setText("Verbinde");
        clientController.close();
        clientController = null;
    }
    private void changeScreen(final String name, final Button button) {
        if (this.locked)
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

    public static ClientController getClientController() {
        return clientController;
    }

    public static String getChosenPlayerName() {
        return nameField.getText();
    }
}

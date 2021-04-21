package de.flojo.jam.screens;

import de.flojo.jam.Main;
import de.flojo.jam.graphics.Button;
import de.flojo.jam.networking.client.ClientController;
import de.flojo.jam.screens.ingame.GameScreen;
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.TextFieldComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;

public class ConnectScreen extends Screen {
    public static final String NAME = "Game-Connect";
    private static final BufferedImage background = Resources.images().get("painted03.jpeg");
    private static final String[] RANDOM_NAMES = new String[]{
            "PeterMeter", "Pain Gain", "H4ns3l", "Joseph", "Achtung Butter!", "Hallo Mami",
            "Jam-Ben", "Niemand", "Du", "Ich", "Wer?", "Name", "Bluhme", "Jonas", "Flo", "W3rW0lf", "Tschonas", "Tschonny",
            "Jaiger", "Nicht du", "Nicht ich", "Humbug", "Wer das liest...", "0x48", "Errör", "Fehlähr", "Wortjoke",
            "Vormorgen", "Nachgestern", "Blumenwiese", "Ostfriese", "Sonnenkind", "Mondmöchtegern", "Jarl", "Karl",
            "Schweizer", "Hinterdupfinger", "Der coole", "Der coolere", "Möchtegern", "Möchte-Gern", "Der mit der Macht", "Der mit dem Geld",
            "Pusteblume", "Narzisse", "Jens", "Jakob", "Jerome", "0x12", "Der lustige", "Hammer Hans", "Hyänen Heini", "Steppenjürgen",
            "Eine Box", "Eine Katze", "Die Rogschie", "Der Tschonny", "Neee", "Jaaaa", "Un", "Lustig", "404", "Spieler A", "Spieler B", "Spieler C", "Spieler D"
    };
    private static final ConnectScreen instance = new ConnectScreen();
    private boolean locked;
    private ClientController clientController;
    private TextFieldComponent nameField;
    private TextFieldComponent portNumber;
    private TextFieldComponent address;
    private Button connect;
    private boolean connected = false;

    private ConnectScreen() {
        super(NAME);
        Game.log().info("Building game connect Screen");
        updatePositions();
    }

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
        TextRenderer.render(g, "Connection status: " + getConnectStatus(), Main.INNER_MARGIN,
                15.0 + g.getFontMetrics().getHeight() + largeHeight);
        TextRenderer.render(g, "Port: ", Main.INNER_MARGIN,
                (Game.window().getHeight() + portNumber.getHeight() + 200) / 2);

        TextRenderer.render(g, "Adr.: ", Main.INNER_MARGIN,
                (Game.window().getHeight() + portNumber.getHeight()) / 2);

        TextRenderer.render(g, "Name: ", Main.INNER_MARGIN,
                (Game.window().getHeight() + portNumber.getHeight() + 400) / 2);

        super.render(g);
    }

    private String getConnectStatus() {
        return clientController == null ? "Not connected" : clientController.getConnectedStatus();
    }

    @Override
    public void prepare() {
        super.prepare();
        Game.window().onResolutionChanged(r -> updatePositions());
        Game.loop().perform(100, this::updatePositions);
        if (connected)
            switchToGameScreen();
        InputController.get().onKeyPressed(KeyEvent.VK_ESCAPE, e -> changeScreen(MenuScreen.NAME), ConnectScreen.NAME);
    }

    private void updatePositions() {
        final double height = Game.window().getResolution().getHeight();
        final double width = Game.window().getResolution().getWidth();
        this.portNumber.setLocation(Main.INNER_MARGIN + 70d, (height + portNumber.getHeight() + 200) / 2 - 26);
        nameField.setLocation(Main.INNER_MARGIN + 55d, (height + nameField.getHeight() + 400) / 2 - 26);
        this.address.setLocation(Main.INNER_MARGIN + 45d, (height + address.getHeight()) / 2 - 26);
        this.connect.setLocation(width - this.connect.getWidth() - 0.5 * Main.INNER_MARGIN,
                height - this.connect.getHeight());
    }

    @Override
    protected void initializeComponents() {
        super.initializeComponents();
        this.connect = new Button("Connect", Main.GUI_FONT_SMALL);
        this.connect.onClicked(c -> {
            if (connected) {
                connected = false;
                updateOnConnected();
            } else {
                connect(b -> {
                    connected = b;
                    updateOnConnected();
                });
            }
        });
        this.getComponents().add(connect);

        // pos will be recalculated
        this.portNumber = new TextFieldComponent(0, 0, 100, 70, ServerSetupScreen.DEFAULT_PORT);
        this.portNumber.setFormat("[0-9]{0,4}");
        this.getComponents().add(portNumber);
        this.address = new TextFieldComponent(0, 0, 600, 70, "localhost");
        this.address.setFormat("[a-zA-Z.0-9]{0,200}");
        this.getComponents().add(address);
        nameField = new TextFieldComponent(0, 0, 600, 70, Game.random().choose(RANDOM_NAMES));
        nameField.setFormat(".{0,25}");
        this.getComponents().add(nameField);
        updatePositions();
    }


    private void updateOnConnected() {
        if (!connected)
            disconnect();
        else
            clientController.getSender().sendHello(nameField.getText());
    }

    private void connect(Consumer<Boolean> onCompleted) {
        this.portNumber.setEnabled(false);
        this.address.setEnabled(false);
        nameField.setEnabled(false);
        this.connect.setText("Disconnect");
        updatePositions();
        try {
            clientController = new ClientController(new URI("ws://" + this.address.getText() + ":" + this.portNumber.getText()), this::onNetworkUpdate);
            clientController.tryConnect(onCompleted);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            // just to be sure
            clientController = null;
        }
    }

    void onNetworkUpdate(String... data) {
        HexStratLogger.log().log(Level.INFO, "Got notified! ({0})", Arrays.toString(data));

        if (data.length == 0)
            return;

        switch (data[0]) {
            case "CLOSED":
                disconnect();
                break;
            case "START":
                switchToGameScreen();
                break;
            default:
                HexStratLogger.log().log(Level.WARNING, "Unknown Data on first Element? ({0})", data[0]);
        }
    }


    private void switchToGameScreen() {
        // prepare correct Data :D
        GameScreen.get().setup(clientController.getContext().getMyPlayerId());
        changeScreen(GameScreen.NAME);
    }

    private void disconnect() {
        this.portNumber.setEnabled(true);
        this.address.setEnabled(true);
        nameField.setEnabled(true);
        this.connect.setText("Connect");
        if (clientController != null)
            clientController.close();
        clientController = null;
    }

    private void changeScreen(final String name) {
        if (this.locked)
            return;

        Game.window().cursor().set(Main.DEFAULT_CURSOR);

        this.locked = true;
        Game.window().getRenderComponent().fadeOut(650);
        Game.loop().perform(950, () -> {
            Game.screens().display(name);
            Game.window().getRenderComponent().fadeIn(650);
            this.locked = false;
        });
    }

    public ClientController getClientController() {
        return clientController;
    }

    public String getChosenPlayerName() {
        return nameField.getText();
    }
}


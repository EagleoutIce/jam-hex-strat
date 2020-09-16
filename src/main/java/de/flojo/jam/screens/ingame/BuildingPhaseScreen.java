package de.flojo.jam.screens.ingame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.logging.Level;

import de.flojo.jam.Main;
import de.flojo.jam.game.GameField;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.client.ClientController;
import de.flojo.jam.networking.messages.BuildChoiceMessage;
import de.flojo.jam.networking.messages.BuildUpdateMessage;
import de.flojo.jam.networking.messages.YouCanBuildMessage;
import de.flojo.jam.screens.ConnectScreen;
import de.flojo.jam.screens.MenuScreen;
import de.flojo.jam.util.BuildChoice;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;

public class BuildingPhaseScreen extends Screen {
    private static final Color P1_COLOR = new Color(45, 173, 215);
    private static final Color P2_COLOR = new Color(141, 45, 215);

    GameField field;
    private PlayerId ourId;
    private String playerTag;
    private ClientController clientController;

    public static final String NAME = "BUILDPHASE";

    private BuildingPhaseScreen() {
        super(NAME);
        Game.log().info("Building Build-phase Screen");
    }

    private static final BuildingPhaseScreen instance = new BuildingPhaseScreen();

    public static BuildingPhaseScreen get() {
        return instance;
    }

    public void setup(PlayerId ourId) {
        this.field = new GameField(this, BuildingPhaseScreen.NAME, ourId);
        this.clientController = ConnectScreen.get().getClientController();
        this.field.updateTerrain(new TerrainMap(clientController.getContext().getTerrain()));
        this.ourId = ourId;
        this.playerTag = this.ourId.ifOne("Spieler 1", "Spieler 2");
        this.clientController.setOnConnectionStateUpdate(this::onNetworkUpdate);
    }

    void onNetworkUpdate(String... data) {
        Game.log().log(Level.FINE, "Got notified! ({0})", Arrays.toString(data));

        if(data.length == 0)
            return;
        
        switch(data[0]) {
            case "CLOSED":
                disconnect();   
                break;
            default:
                Game.log().log(Level.WARNING, "Unknown Data on first Element? ({0})", data[0]);
        }
    }

    private void disconnect() {
        Game.screens().display(MenuScreen.NAME);
    }

    @Override
    public void prepare() {
        super.prepare();
    }

    public void buildOne(YouCanBuildMessage message) {
        field.allowOneBuild(this::onBuild, message.getMoneyLeft());
    }

    private void onBuild(BuildChoice choice) {
        clientController.send(new BuildChoiceMessage(null, choice.getSelectedPosition(), choice.getChosenTerrain(),
                choice.getChosenCreature(), choice.getChosenTrap(), ""));
    }

    @Override
    public void render(final Graphics2D g) {
        if (field == null)
            return;
        field.render(g);
        g.setFont(Main.GUI_FONT_SMALL);
        g.setColor(field.isOurTurn() ? Color.GREEN : Color.WHITE);
        TextRenderer.render(g, playerTag,
                Game.window().getWidth() - Main.INNER_MARGIN - TextRenderer.getWidth(g, playerTag), 60, true);
        final String p1 = "Player 1: " + clientController.getContext().getP1Name();
        final String p2 = "Player 2: " + clientController.getContext().getP2Name();
        g.setColor(P1_COLOR);
        g.setFont(Main.TEXT_STATUS);
        int w = (int) Math.max(TextRenderer.getWidth(g, p1), TextRenderer.getWidth(g, p2));
        TextRenderer.render(g, p1, Game.window().getWidth() - Main.INNER_MARGIN - w, 120, true);
        g.setColor(P2_COLOR);
        TextRenderer.render(g, p2, Game.window().getWidth() - Main.INNER_MARGIN - w, 160, true);
        super.render(g);
    }

	public void updateMap(BuildUpdateMessage message) {
        field.updateTerrain(message.getMap());
	}

}

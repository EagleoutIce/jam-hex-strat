package de.flojo.jam.screens.ingame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import de.flojo.jam.Main;
import de.flojo.jam.game.GameField;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.creature.ActionType;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.creature.controller.CreatureActionController;
import de.flojo.jam.game.creature.skills.SkillId;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.client.ClientController;
import de.flojo.jam.networking.messages.BuildChoiceMessage;
import de.flojo.jam.networking.messages.BuildUpdateMessage;
import de.flojo.jam.networking.messages.GameStartMessage;
import de.flojo.jam.networking.messages.ItIsYourTurnMessage;
import de.flojo.jam.networking.messages.NextRoundMessage;
import de.flojo.jam.networking.messages.TurnActionMessage;
import de.flojo.jam.networking.messages.YouCanBuildMessage;
import de.flojo.jam.screens.ConnectScreen;
import de.flojo.jam.screens.MenuScreen;
import de.flojo.jam.util.BuildChoice;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;

public class GameScreen extends Screen {
    private static final Color P1_COLOR = new Color(45, 173, 215);
    private static final Color P2_COLOR = new Color(141, 45, 215);

    GameField field;
    private PlayerId ourId;
    private String playerTag;
    private ClientController clientController;
    private int currentRound = 0;

    public static final String NAME = "BUILDPHASE";

    private GameScreen() {
        super(NAME);
        Game.log().info("Building Build-phase Screen");
    }

    private static final GameScreen instance = new GameScreen();

    public static GameScreen get() {
        return instance;
    }

    public void setup(PlayerId ourId) {
        this.field = new GameField(this, GameScreen.NAME, ourId);
        this.clientController = ConnectScreen.get().getClientController();
        this.field.updateTerrain(new TerrainMap(clientController.getContext().getTerrain()));
        this.ourId = ourId;
        this.playerTag = this.ourId.ifOne("Spieler 1", "Spieler 2");
        this.clientController.setOnConnectionStateUpdate(this::onNetworkUpdate);
    }

    void onNetworkUpdate(String... data) {
        Game.log().log(Level.FINE, "Got notified! ({0})", Arrays.toString(data));

        if (data.length == 0)
            return;

        switch (data[0]) {
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
        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT_SMALL);
        if (currentRound == 0) {
            final String buildPhase = "Bauphase";
            TextRenderer.render(g, buildPhase, Game.window().getCenter().getX() - TextRenderer.getWidth(g, buildPhase)/2,
                    60, true);
        } else if (currentRound > 0) {
            final String roundText = "Runde: " + currentRound;
            TextRenderer.render(g, roundText,
                    Game.window().getCenter().getX() - TextRenderer.getWidth(g, roundText) / 2, 60, true);
        }
        super.render(g);
    }

    public void updateMap(BuildUpdateMessage message) {
        field.updateTerrain(message.getMap());
    }

    public void initGameStart(GameStartMessage message) {
        field.updateTerrain(message.getTerrain());
        field.updateCreatures(message.getCreatures());
        field.updateTraps(message.getTraps());
    }

    public void nextRound(NextRoundMessage message) {
        getFactory().resetAll();
        field.getPresenter().update();
        this.currentRound = message.getCurrentRound();
    }

    public void ourTurn(ItIsYourTurnMessage message) {
        field.allowOneTurn(this::onTurnSkip, this::onTurnMove, this::onTurnSkill, message);
    }

    private void onTurnSkip(BoardCoordinate creaturePosition) {
        Game.log().log(Level.INFO, "Skip for creature on {0}", creaturePosition);
        clientController.send(new TurnActionMessage(null, ActionType.SKIP, creaturePosition, null, null));
    }

    private void onTurnMove(BoardCoordinate from, List<BoardCoordinate> moveTargets) {
        Game.log().log(Level.INFO, "Move from {0} to {1}", new Object[] { from, moveTargets });
        clientController.send(new TurnActionMessage(null, ActionType.MOVEMENT, from, moveTargets, null));
    }

    private void onTurnSkill(BoardCoordinate from, BoardCoordinate target, SkillId skill) {
        Game.log().log(Level.INFO, "Skill {2} from {0}, targeting: {1}", new Object[] { from, target, skill });
        clientController.send(new TurnActionMessage(null, ActionType.SKILL, from, List.of(target), skill));
    }

    public void performTurn(TurnActionMessage message) {
        Optional<Creature> mayCreature = getFactory().get(message.getFrom());
        if (mayCreature.isEmpty()) {
            Game.log().log(Level.SEVERE, "ActionMessage could not be performed, as no performer was found in: {0}",
                    message.toJson());
            return;
        }
        final Creature creature = mayCreature.get();

        switch (message.getAction()) {
            case MOVEMENT:
                new Thread(() -> processMovement(message, creature)).start();
                break;
            case SKILL:
                Optional<Creature> mayTarget = getFactory().get(message.getTarget());
                if (mayTarget.isEmpty()) {
                    Game.log().log(Level.SEVERE,
                            "ActionMessage could not be performed, as skill target was no creature in: {0}", message);
                    return;
                }
                creature.useSkill(getBoard(), message.getSkillId(), mayTarget.get());
                break;
            case SKIP:
                creature.skip();
                break;
            default:
            case NONE:
        }
    }

    private void processMovement(TurnActionMessage message, final Creature creature) {
        List<BoardCoordinate> targets = message.getTargets();
        for (BoardCoordinate target : targets) {
            Game.log().log(Level.INFO, "Animating move to: {0} (int: {1}); for {2}",
                    new Object[] { target, targets, creature });
            CreatureActionController.processMovementBlocking(field.getTraps(), creature, getBoard().getTile(target));
        }
    }

    private Board getBoard() {
        return field.getBoard();
    }

    private CreatureFactory getFactory() {
        return field.getFactory();
    }

}

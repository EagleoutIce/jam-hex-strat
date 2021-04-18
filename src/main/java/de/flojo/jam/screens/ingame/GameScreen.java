package de.flojo.jam.screens.ingame;

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
import de.flojo.jam.networking.messages.GameOverMessage;
import de.flojo.jam.networking.messages.GameStartMessage;
import de.flojo.jam.networking.messages.ItIsYourTurnMessage;
import de.flojo.jam.networking.messages.NextRoundMessage;
import de.flojo.jam.networking.messages.TurnActionMessage;
import de.flojo.jam.networking.messages.YouCanBuildMessage;
import de.flojo.jam.screens.ConnectScreen;
import de.flojo.jam.screens.MenuScreen;
import de.flojo.jam.util.BuildChoice;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class GameScreen extends Screen {
    public static final BufferedImage PLAYER_VIEW_TURN_P1 = Resources.images().get("ui/spieleranzeige_player1.png");
    public static final BufferedImage PLAYER_VIEW_TURN_P2 = Resources.images().get("ui/spieleranzeige_player2.png");
    public static final int RIGHT_WIDTH = Math.max(PLAYER_VIEW_TURN_P1.getWidth(), PLAYER_VIEW_TURN_P2.getWidth());
    public static final BufferedImage BUILD_PHASE = Resources.images().get("ui/phasenanzeige_building_phase.png");
    public static final BufferedImage MAIN_PHASE = Resources.images().get("ui/phasenanzeige_main_phase.png");
    public static final String NAME = "BUILDPHASE";
    private static final Color P1_COLOR = new Color(45, 173, 215);
    private static final Color P2_COLOR = new Color(141, 45, 215);
    private static final GameScreen instance = new GameScreen();
    final int MAX_NAME_LENGTH = 7;
    GameField field;
    private PlayerId ourId;
    private ClientController clientController;
    private int currentRound = 0;
    private boolean locked;

    private GameScreen() {
        super(NAME);
        Game.log().info("Building Build-phase Screen");
    }

    public static GameScreen get() {
        return instance;
    }

    public void setup(PlayerId ourId) {
        this.field = new GameField(this, GameScreen.NAME, ourId);
        this.clientController = ConnectScreen.get().getClientController();
        this.field.updateTerrain(new TerrainMap(clientController.getContext().getTerrain()));
        this.ourId = ourId;
        // this.playerTag = this.ourId.ifOne("Spieler 1", "Spieler 2");
        this.clientController.setOnConnectionStateUpdate(this::onNetworkUpdate);
        InputController.get().onMoved(this::lockOnOver, GameScreen.NAME);
    }

    void onNetworkUpdate(String... data) {
        Game.log().log(Level.FINE, "Got notified! ({0})", Arrays.toString(data));

        if (data.length == 0)
            return;

        if ("CLOSED".equals(data[0])) {
            disconnect(false);
        } else {
            Game.log().log(Level.WARNING, "Unknown Data on first Element? ({0})", data[0]);
        }
    }

    private void disconnect(boolean ask) {
        if (ask && askStupidUserForConfirmationOnExit())
            return;
        clientController.close();
        changeScreen(MenuScreen.NAME);
    }

    @Override
    public void prepare() {
        super.prepare();
        InputController.get().onKeyPressed(KeyEvent.VK_ESCAPE, e -> disconnect(true), ConnectScreen.NAME);
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
        PlayerId turnPlayerId = field.isOurTurn() ? ourId : ourId.other();

        ImageRenderer.render(g, turnPlayerId.ifOne(PLAYER_VIEW_TURN_P1, PLAYER_VIEW_TURN_P2), Game.window().getWidth() - (double) RIGHT_WIDTH, 0);

        g.setFont(Main.TEXT_STATUS);
        g.setColor(Color.WHITE);
        final String roundText = "Round: " + currentRound;
        TextRenderer.render(g, roundText,
                Game.window().getWidth() - 105 - TextRenderer.getWidth(g, roundText) / 2, 38d, true);
        String p1 = ourId.ifOne(">", "") + clientController.getContext().getP1Name();
        p1 = p1.substring(0, Math.min(p1.length(), MAX_NAME_LENGTH)) + (p1.length() > MAX_NAME_LENGTH ? "..." : "");
        String p2 = ourId.ifTwo(">", "") + clientController.getContext().getP2Name();
        p2 = p2.substring(0, Math.min(p2.length(), MAX_NAME_LENGTH)) + (p2.length() > MAX_NAME_LENGTH ? "..." : "");
        g.setColor(P1_COLOR);
        g.setFont(Main.TEXT_STATUS);
        TextRenderer.render(g, p1, Game.window().getWidth() - 190d, 120, true);
        g.setColor(P2_COLOR);
        TextRenderer.render(g, p2, Game.window().getWidth() - 190d, 160, true);
        g.setColor(Color.WHITE);
        g.setFont(Main.GUI_FONT_SMALL);
        if (currentRound == 0) {
            ImageRenderer.render(g, BUILD_PHASE, Game.window().getCenter().getX() - BUILD_PHASE.getWidth() / 2d, 0);
        } else if (currentRound > 0) {
            ImageRenderer.render(g, MAIN_PHASE, Game.window().getCenter().getX() - MAIN_PHASE.getWidth() / 2d, 0);
        }
        super.render(g);
    }


    private void lockOnOver(MouseEvent me) {
        if (me.getX() >= Game.window().getWidth() - PLAYER_VIEW_TURN_P1.getWidth() &&
                me.getY() <= PLAYER_VIEW_TURN_P1.getHeight()) {
            field.getBoard().doNotHover();
        } else {
            field.getBoard().doHover();
        }
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
        Game.log().log(Level.INFO, "Move from {0} to {1}", new Object[]{from, moveTargets});
        clientController.send(new TurnActionMessage(null, ActionType.MOVEMENT, from, moveTargets, null));
    }

    private void onTurnSkill(BoardCoordinate from, BoardCoordinate target, SkillId skill) {
        Game.log().log(Level.INFO, "Skill {2} from {0}, targeting: {1}", new Object[]{from, target, skill});
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
                            "ActionMessage could not be performed, as skill target was no creature in: {0}", message.toJson());
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
                    new Object[]{target, targets, creature});
            CreatureActionController.processMovementBlocking(field.getTraps(), creature, getBoard().getTile(target));
        }
    }

    private Board getBoard() {
        return field.getBoard();
    }

    private CreatureFactory getFactory() {
        return field.getFactory();
    }

    private boolean askStupidUserForConfirmationOnExit() {
        final Object[] options = {"Yes", "No"};
        final int n = JOptionPane.showOptionDialog(Game.window().getRenderComponent(),
                "Are you sure, that you want to leave the game?", null, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        return n != 0;
    }

    // TODO: refactor screen hierarchy
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

    public void gameOver(GameOverMessage message) {
        JOptionPane.showMessageDialog(Game.window().getRenderComponent(),
                "The game is over. The winner is: " + message.getWinnerId());
    }

}

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
import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.flojo.jam.game.creature.skills.JsonDataOfSkill;
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
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.InputController;
import de.flojo.jam.util.ToolTip;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.sound.Sound;

import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class GameScreen extends Screen {
    public static final BufferedImage PLAYER_VIEW_TURN_P1 = Resources.images().get("ui/spieleranzeige_player1.png");
    public static final BufferedImage PLAYER_VIEW_TURN_P2 = Resources.images().get("ui/spieleranzeige_player2.png");
    public static final int RIGHT_WIDTH = Math.max(PLAYER_VIEW_TURN_P1.getWidth(), PLAYER_VIEW_TURN_P2.getWidth());
    public static final BufferedImage BUILD_PHASE = Resources.images().get("ui/phasenanzeige_building_phase.png");
    public static final BufferedImage MAIN_PHASE = Resources.images().get("ui/phasenanzeige_main_phase.png");
    public static final BufferedImage WON = Resources.images().get("ui/gewonnen_sign.png");
    public static final BufferedImage LOST = Resources.images().get("ui/verloren_sign.png");
    public static final Sound TURN_START = Resources.sounds().get("audio/sound/round_start.wav");
    public static final String NAME = "BUILDPHASE";
    public static final Color P1_COLOR = new Color(45, 173, 215);
    public static final Color P2_COLOR = new Color(141, 45, 215);
    private static final GameScreen instance = new GameScreen();
    private static final int MAX_NAME_LENGTH = 7;
    private final AtomicBoolean gameOver = new AtomicBoolean();
    private final AtomicBoolean weWon = new AtomicBoolean();
    GameField field;
    private PlayerId ourId;
    private ClientController clientController;
    private int currentRound = 0;
    private boolean locked;

    private GameScreen() {
        super(NAME);
        HexStratLogger.log().info("Building Build-phase Screen");
    }

    public static GameScreen get() {
        return instance;
    }

    public void setup(PlayerId ourId) {
        this.field = new GameField(this, GameScreen.NAME, ourId);
        this.clientController = ConnectScreen.get().getClientController();
        this.field.updateTerrain(new TerrainMap(clientController.getContext().getTerrain()));
        this.ourId = ourId;
        this.clientController.setOnConnectionStateUpdate(this::onNetworkUpdate);
        InputController.get().onMoved(this::lockOnOver, GameScreen.NAME);
    }

    void onNetworkUpdate(String... data) {
        HexStratLogger.log().log(Level.INFO, "Got notified! ({0})", Arrays.toString(data));

        if (data.length == 0)
            return;

        if ("CLOSED".equals(data[0])) {
            disconnect(false);
        } else {
            HexStratLogger.log().log(Level.WARNING, "Unknown Data on first Element? ({0})", data[0]);
        }
    }

    private void disconnect(boolean ask) {
        if (ask && askStupidUserForConfirmationOnExit())
            return;
        clientController.close();
        this.field.reset();
        changeScreen(MenuScreen.NAME);
    }

    @Override
    public void prepare() {
        super.prepare();
        field.getBoard().resetZoom();
        InputController.get().onKeyPressed(KeyEvent.VK_ESCAPE, e -> disconnect(true), GameScreen.NAME);
    }

    public void buildOne(YouCanBuildMessage message) {
        playTurnPing();
        field.allowOneBuild(this::onBuild, message.getMoneyLeft());
    }

    private void onBuild(BuildChoice choice) {
        clientController.send(new BuildChoiceMessage(null, choice.getSelectedPosition(), choice.getChosenTerrain(),
                                                     choice.getChosenCreature(), choice.getChosenTrap(),
                                                     choice.isGift(), ""));
    }

    @Override
    public void render(final Graphics2D g) {
        if (field == null)
            return;
        field.render(g);

        PlayerId turnPlayerId = field.isOurTurn() ? ourId : ourId.other();

        ImageRenderer.render(g, turnPlayerId.ifOne(PLAYER_VIEW_TURN_P1, PLAYER_VIEW_TURN_P2),
                             Game.window().getWidth() - (double) RIGHT_WIDTH, 0);

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
        if (gameOver.get())
            renderGameOverBanner(g);
        super.render(g);
        List<ToolTip<GuiComponent>> presenterToolTips = field.getPresenter().getToolTips();
        if (presenterToolTips != null)
            presenterToolTips.forEach(t -> t.render(g));
        List<ToolTip<GuiComponent>> buildPhaseToolTips = field.getBuildingPhaseButtons().getToolTips();
        if (buildPhaseToolTips != null)
            buildPhaseToolTips.forEach(t -> t.render(g));
    }

    private void renderGameOverBanner(Graphics2D g) {
        final Point2D center = Game.window().getCenter();
        final boolean won = weWon.get();
        final BufferedImage banner = won ? WON : LOST;
        final double x = center.getX() - banner.getWidth() / 2d;
        final double y = center.getY() - banner.getHeight() / 2d;
        ImageRenderer.render(g, banner, x, y);
        final String text = won ? "Victory" : "Loose";
        g.setFont(Main.GUI_FONT_LARGE);
        TextRenderer.render(g, text,
                            center.getX() - TextRenderer.getWidth(g, text) / 2d, center.getY() + (won ? 43d : 18d),
                            true);
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
        field.getBoard().nextRound(field.getFactory(), field.getSpawner());
        this.currentRound = message.getCurrentRound();
    }

    public void ourTurn(ItIsYourTurnMessage message) {
        playTurnPing();
        field.allowOneTurn(this::onTurnSkip, this::onTurnMove, this::onTurnSkill, message);
    }

    private void playTurnPing() {
        new Thread(() -> Game.audio().playSound(TURN_START, false)).start();
    }

    private void onTurnSkip(BoardCoordinate creaturePosition) {
        HexStratLogger.log().log(Level.INFO, "Skip for creature on {0}", creaturePosition);
        clientController.send(new TurnActionMessage(null, ActionType.SKIP, creaturePosition, null, null));
    }

    private void onTurnMove(BoardCoordinate from, List<BoardCoordinate> moveTargets) {
        HexStratLogger.log().log(Level.INFO, "Move from {0} to {1}", new Object[]{from, moveTargets});
        clientController.send(new TurnActionMessage(null, ActionType.MOVEMENT, from, moveTargets, null));
    }

    private void onTurnSkill(BoardCoordinate from, BoardCoordinate target, JsonDataOfSkill skill) {
        HexStratLogger.log().log(Level.INFO, "Skill {2} from {0}, targeting: {1}", new Object[]{from, target, skill});
        clientController.send(new TurnActionMessage(null, ActionType.SKILL, from, List.of(target), skill));
    }

    public void performTurn(TurnActionMessage message) {
        Optional<Creature> mayCreature = getFactory().get(message.getFrom());
        if (mayCreature.isEmpty()) {
            HexStratLogger.log().log(Level.SEVERE,
                                     "ActionMessage could not be performed, as no performer was found in: {0}",
                                     message.toJson());
            return;
        }
        final var creature = mayCreature.get();

        switch (message.getAction()) {
            case MOVEMENT:
                new Thread(() -> processMovement(message, creature)).start();
                break;
            case SKILL:
                new Thread(() -> processSkill(message, creature)).start();
                break;
            case SKIP:
                creature.skip();
                break;
            default:
            case NONE:
        }
    }

    private void processSkill(TurnActionMessage message, Creature creature) {
        JsonDataOfSkill skillId = message.getSkillData();
        Optional<AbstractSkill> maySkill = creature.getSkill(skillId);
        if (maySkill.isEmpty()) {
            HexStratLogger.log().log(Level.SEVERE,
                                     "ActionMessage could not be performed, as creature {1} does not possess skill requested by: {0}",
                                     new Object[]{message.toJson(), creature});
            return;
        }
        AbstractSkill skill = maySkill.get();
        switch (skill.getTarget()) {
            case CREATURE:
                Optional<Creature> mayTargetCreature = getFactory().get(message.getTarget());
                if (mayTargetCreature.isEmpty()) {
                    HexStratLogger.log().log(Level.SEVERE,
                                             "ActionMessage could not be performed, as skill target needed to be, but was no creature in: {0}",
                                             message.toJson());
                    return;
                }
                creature.useSkill(getBoard(), skill, mayTargetCreature.get());
                creature.getAttributes().useAp(skill.getCost());
                break;
            case TILE:
                final var targetTile = getBoard().getTile(message.getTarget());
                creature.useSkill(getBoard(), skill, targetTile);
                creature.getAttributes().useAp(skill.getCost());
                break;
            default:
                HexStratLogger.log().log(Level.SEVERE,
                                         "ActionMessage could not be performed, as skill target-type was invalid ({1}): {0}",
                                         new Object[]{message.toJson(), skill.getTarget()});
        }
    }

    private void processMovement(TurnActionMessage message, final Creature creature) {
        List<BoardCoordinate> targets = message.getTargets();
        for (BoardCoordinate target : targets) {
            HexStratLogger.log().log(Level.INFO, "Animating move to: {0} (int: {1}); for {2}",
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
                                                   "Are you sure, that you want to leave the game?", null,
                                                   JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                                   options, options[1]);
        return n != 0;
    }

    // TODO: refactor screen hierarchy
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

    public void gameOver(GameOverMessage message) {
        gameOver.set(true);
        weWon.set(Objects.equals(ourId, message.getWinnerId()));
    }

}

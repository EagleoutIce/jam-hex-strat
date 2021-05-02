package de.flojo.jam.game;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.terrain.Architect;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.traps.TrapJson;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.creature.CreatureJson;
import de.flojo.jam.game.creature.IAction;
import de.flojo.jam.game.creature.IActionMove;
import de.flojo.jam.game.creature.IActionSkill;
import de.flojo.jam.game.creature.IActionSkip;
import de.flojo.jam.game.creature.skills.JsonDataOfSkill;
import de.flojo.jam.game.creature.skills.SkillsPresenter;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.BuildingPhaseButtonPresenter;
import de.flojo.jam.networking.messages.ItIsYourTurnMessage;
import de.flojo.jam.screens.ingame.GameScreen;
import de.flojo.jam.util.BuildChoice;
import de.flojo.jam.util.IProvideContext;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.gui.screens.Screen;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class GameField implements IRenderable, IProvideContext {
    public static final String FIELD_BACKGROUND = "field-background.jpg";
    public static final int BOARD_HEIGHT = 33;
    public static final int BOARD_WIDTH = 24;

    private final Board board;
    private final Architect architect;
    private final CreatureFactory factory;
    private final TrapSpawner spawner;
    private final SkillsPresenter presenter;
    private final BuildingPhaseButtonPresenter buildingPhaseButtons;
    private PlayerId owner;
    private int moneyLeft;

    private boolean canBuild = false;
    private boolean ourTurn = false;

    public GameField(final Screen target, final String screenName, PlayerId owner) {
        this.owner = owner;
        this.board = new Board("configs/empty.terrain", screenName);
        this.spawner = new TrapSpawner(board, screenName);
        this.factory = new CreatureFactory(screenName, board, spawner.getTraps());
        this.architect = new Architect(board, factory, spawner);
        this.architect.setPlayerId(owner);
        this.presenter = new SkillsPresenter(target, board, factory, spawner, owner, screenName);
        // initially off; just to be sure
        this.presenter.disable();
        InputController.get().onClicked(this::processMouse, screenName);
        buildingPhaseButtons = new BuildingPhaseButtonPresenter(target, this, owner);
        buildingPhaseButtons.disable();
    }

    void processMouse(MouseEvent e) {
        if (board.doesNotHover() || !ourTurn)
            return;

        if (canBuild) {
            buildingPhaseButtons.processMouse(e);
        }
    }

    public void updateTerrain(TerrainMap map) {
        this.board.setTerrainMap(map);
        // this.board.doHover()
    }

    public String getTerrainName() {
        return board.getTerrainMap().getTerrain().getName();
    }

    public void reset() {
        architect.clearField();
        presenter.reset();
        factory.removeAll();
        spawner.removeAll();
        buildingPhaseButtons.reset();
    }

    @Override
    public void render(Graphics2D g) {
        board.jointRender(g, owner, factory, spawner);
        if(isOurTurn()) {
            g.setColor(owner.ifOne(GameScreen.P1_COLOR, GameScreen.P2_COLOR));
            g.setStroke(new BasicStroke(7));
            g.drawRect(0, 0, Game.window().getWidth() - 10, Game.window().getHeight()-Game.window().getHostControl().getInsets().top- 7);
            g.setStroke(new BasicStroke());
        }
        buildingPhaseButtons.render(g);
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public CreatureFactory getFactory() {
        return factory;
    }

    @Override
    public TrapSpawner getSpawner() {
        return spawner;
    }

    @Override
    public Architect getArchitect() {
        return architect;
    }

    @Override
    public SkillsPresenter getPresenter() {
        return presenter;
    }

    public BuildingPhaseButtonPresenter getBuildingPhaseButtons() {
        return buildingPhaseButtons;
    }

    public void allowOneBuild(Consumer<BuildChoice> onChoice, int moneyLeft) {
        this.moneyLeft = moneyLeft;
        canBuild = true;
        ourTurn = true;
        buildingPhaseButtons.enable();
        buildingPhaseButtons.setCurrentBuildConsumer(b -> {
            canBuild = false;
            onChoice.accept(b);
            buildingPhaseButtons.disable();
            if (b.getChosenCreature() != null)
                b.getChosenCreature().getSoundPool().play();
            ourTurn = false;
            buildingPhaseButtons.setCurrentBuildConsumer(null);
        });
    }

    public boolean isOurTurn() {
        return ourTurn;
    }

    @Override
    public int getMoneyLeft() {
        return moneyLeft;
    }

    public void setPlayerId(PlayerId id) {
        presenter.setPlayerId(id);
        architect.setPlayerId(id);
        owner = id;
    }

    public void updateCreatures(List<CreatureJson> creatures) {
        factory.updateCreatures(creatures, owner);
    }

    public void updateTraps(List<TrapJson> traps) {
        spawner.updateTraps(traps);
    }

    public void allowOneTurn(IActionSkip skip, IActionMove move, IActionSkill skill, ItIsYourTurnMessage message) {
        // do smth with message?
        ourTurn = true;
        presenter.enable();
        presenter.setOnActionConsumer(new IAction() {
            @Override
            public void onSkip(BoardCoordinate creaturePosition) {
                skip.onSkip(creaturePosition);
                cleanup();
            }

            @Override
            public void onMove(BoardCoordinate from, List<BoardCoordinate> targets) {
                move.onMove(from, targets);
                cleanup();

            }

            @Override
            public void onSkill(BoardCoordinate from, BoardCoordinate target, JsonDataOfSkill skillId) {
                skill.onSkill(from, target, skillId);
                cleanup();
            }

            private void cleanup() {
                ourTurn = false;
                presenter.disable();
                presenter.setOnActionConsumer(null);
            }
        });
    }

}

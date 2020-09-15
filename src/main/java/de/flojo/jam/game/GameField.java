package de.flojo.jam.game;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.terrain.Architect;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.creature.skills.SkillsPresenter;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.BuildingPhaseButtonPresenter;
import de.flojo.jam.util.BuildChoice;
import de.flojo.jam.util.IProvideContext;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.gui.screens.Screen;

// The superduperwuperclass
public class GameField implements IRenderable, IProvideContext {
    public static final String FIELD_BACKGROUND = "field-background.png";
    public static final int BOARD_HEIGHT = 33;
    public static final int BOARD_WIDTH = 24;

    private final String screenName;

    private Board board;
    private Architect architect;
    private CreatureFactory factory;
    private TrapSpawner spawner;
    private SkillsPresenter presenter;
    private PlayerId owner;

    private BuildingPhaseButtonPresenter buildingPhaseButtons;

    private boolean canBuild = false;
    private boolean ourTurn = false;

    // TODO: present our id to the player. present turn etc.

    public GameField(final Screen target, final String screenName, PlayerId owner) {
        this.screenName = screenName;
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
        if (!board.doesHover() || !ourTurn)
            return;

        if(canBuild) {
            buildingPhaseButtons.processMouse(e);
        }
    
    }

    public void updateTerrain(TerrainMap map) {
        this.board.setTerrainMap(map);
        this.board.doHover();
    }

    public String getTerrainName() {
        return board.getTerrainMap().getTerrain().getName();
    }

    public void reset() {
        architect.clearField();
        presenter.disable();
        factory.removeAll();
        spawner.removeAll();
    }

    @Override
    public void render(Graphics2D g) {
        board.jointRender(g, owner, factory, spawner);
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

    public void allowOneBuild(Consumer<BuildChoice> onChoice) {
        canBuild = true;
        ourTurn = true;
        buildingPhaseButtons.enable();
        buildingPhaseButtons.setCurrentBuildConsumer(b -> {
            canBuild = false;
            onChoice.accept(b);
            buildingPhaseButtons.disable();
            ourTurn = false;
            buildingPhaseButtons.setCurrentBuildConsumer(null);
        });
    }

    public boolean isOurTurn() {
        return ourTurn;
    }

}

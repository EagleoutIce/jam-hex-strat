package de.flojo.jam.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.highlighting.ImprintHighlighter;
import de.flojo.jam.game.board.highlighting.SimpleHighlighter;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.terrain.management.TerrainImprint;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.creature.ISummonCreature;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.screens.ingame.BuildingPhaseScreen;
import de.flojo.jam.util.BuildChoice;
import de.flojo.jam.util.IProvideContext;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;

public class BuildingPhaseButtonPresenter implements IRenderable {

    private static final BufferedImage SIDEBAR = Resources.images().get("ui/sidebar.png");

    private PlayerId ourId;
    private IProvideContext context;
    private Screen screen;

    private List<ImageButton> terrainButtons;
    private List<ImageButton> creatureButtons;
    private List<ImageButton> trapButtons;

    private Consumer<BuildChoice> currentBuildConsumer;

    private TerrainId currentTerrain = null;
    private ISummonCreature currentCreature = null;
    private TrapId currentTrapId = null;

    private boolean enabled = false;

    private enum BuildingPhaseSelectionMode {
        TERRAIN, CREATURE, TRAP
    }

    private BuildingPhaseSelectionMode selectionMode = BuildingPhaseSelectionMode.TERRAIN;

    public BuildingPhaseButtonPresenter(Screen screen, IProvideContext context, PlayerId ourId) {
        this.screen = screen;
        this.ourId = ourId;
        this.context = context;
        this.terrainButtons = new ArrayList<>();
        populateTerrainButtons();
        this.creatureButtons = new ArrayList<>();
        this.trapButtons = new ArrayList<>();
        this.currentBuildConsumer = null;
        InputController.get().onMoved(this::lockOnOver, BuildingPhaseScreen.NAME);
    }

    private void lockOnOver(MouseEvent me) {
        if (enabled && me.getX() <= SIDEBAR.getWidth()) {
            context.getBoard().doNotHover();
        } else {
            context.getBoard().doHover();
        }
    }

    private void populateTerrainButtons() {
        // Populate TerrainButtons
        TerrainId[] terrains = TerrainId.values();
        for (int i = 0; i < terrains.length; i++) {
            TerrainId t = terrains[i];
            if (t == TerrainId.T_EMPTY)
                continue;

            ImageButton imgBt = new ImageButton(70d, 70d, Main.INNER_MARGIN, i * 80d - 35d,
                    t.getImprint().getBaseResource(), t.getName(), Main.TEXT_NORMAL);
            terrainButtons.add(imgBt);
            imgBt.onClicked(c -> {
                this.currentCreature = null;
                this.currentTerrain = t;
                this.selectionMode = BuildingPhaseSelectionMode.TERRAIN;

                TerrainImprint imprint = t.getImprint();
                if (imprint.hasBaseResource()) {
                    Game.window().cursor().setVisible(true);
                    Game.window().cursor().set(t.getImprint().getBaseResource());
                    context.getBoard().setHighlightMask(new ImprintHighlighter(imprint));
                    Game.window().cursor().showDefaultCursor();
                } else {
                    resetCursor();
                }
            });
            screen.getComponents().add(imgBt);
            imgBt.prepare();
            imgBt.setTextAlign(Align.CENTER);
        }

    }

    // TODO: position
    // TODO: disable hoverover

    public void disable() {
        terrainButtons.forEach(GuiComponent::suspend);
        screen.getComponents().removeAll(terrainButtons);
        TerrainId currentTerrain = null;
        ISummonCreature currentCreature = null;
        TrapId currentTrapId = null;
        enabled = false;
        resetCursor();
    }

    public void enable() {
        terrainButtons.forEach(ImageButton::prepare);
        screen.getComponents().addAll(terrainButtons);
        enabled = true;
    }

    private void resetCursor() {
        Game.window().cursor().set(Main.DEFAULT_CURSOR);
        context.getBoard().setHighlightMask(SimpleHighlighter.get());
    }

    public void setCurrentBuildConsumer(Consumer<BuildChoice> currentBuildConsumer) {
        this.currentBuildConsumer = currentBuildConsumer;
    }

    @Override
    public void render(Graphics2D g) {
        if (!enabled)
            return;

        ImageRenderer.render(g, SIDEBAR, 0, 0);
    }

    public void processMouse(MouseEvent e) {
        switch (selectionMode) {
            case CREATURE:
                summonCreature(e);
                break;
            case TERRAIN:
                plantTile(e);
                break;
            case TRAP:
                spawnTrap(e);
                break;
            default:
                break;
        }
    }

    private boolean intersectsWithButton(Point p) {
        for (ImageButton imageButton : terrainButtons) {
            if (imageButton.getBoundingBox().contains(p))
                return true;
        }
        return false;
    }

    private void plantTile(MouseEvent c) {
        if (this.selectionMode != BuildingPhaseSelectionMode.TERRAIN || context.getArchitect() == null) {
            return;
        }

        if (currentTerrain == null || currentTerrain == TerrainId.T_EMPTY)
            return;

        if (c.getButton() != MouseEvent.BUTTON1)
            return;

        Point p = c.getPoint();
        if (intersectsWithButton(p))
            return;
        Tile t = context.getBoard().findTile(p);
        if (t == null)
            return;

        if (context.getArchitect().placeImprint(t.getCoordinate(), currentTerrain.getImprint())
                && currentBuildConsumer != null)
            currentBuildConsumer.accept(new BuildChoice(currentTerrain, null, null, t.getCoordinate()));
    }

    private void spawnTrap(MouseEvent c) {

    }

    private void summonCreature(MouseEvent c) {

    }

}

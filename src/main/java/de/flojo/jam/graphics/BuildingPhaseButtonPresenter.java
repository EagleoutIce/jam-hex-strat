package de.flojo.jam.graphics;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.highlighting.ImprintHighlighter;
import de.flojo.jam.game.board.highlighting.SimpleHighlighter;
import de.flojo.jam.game.board.terrain.TerrainTile;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.terrain.management.TerrainImprint;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.board.traps.TrapImprint;
import de.flojo.jam.game.creature.CreatureId;
import de.flojo.jam.game.creature.ISummonCreature;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.renderer.IRenderData;
import de.flojo.jam.screens.ingame.GameScreen;
import de.flojo.jam.util.BuildChoice;
import de.flojo.jam.util.IProvideContext;
import de.flojo.jam.util.InputController;
import de.flojo.jam.util.ToolTip;
import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class BuildingPhaseButtonPresenter implements IRenderable {

    public static final BufferedImage MONEY_SYMBOL = Imaging.scale(Resources.images().get("ui/money.png"), 30, 30,
                                                                   true);
    private final PlayerId ourId;
    private final IProvideContext context;
    private final Screen screen;
    private final List<ImageButton> terrainButtons;
    private final List<ImageButton> creatureButtons;
    private final List<ImageButton> trapButtons;
    private final Button giftButton;
    private BufferedImage sidebar;
    private boolean summonedCreature = false;
    private Consumer<BuildChoice> currentBuildConsumer;
    private List<ToolTip<?>> toolTips;

    private TerrainId currentTerrain = null;
    private ISummonCreature currentCreature = null;
    private CreatureId currentCreatureId = null;
    private TrapId currentTrapId = null;

    private boolean enabled = false;
    private BuildingPhaseSelectionMode selectionMode = BuildingPhaseSelectionMode.TERRAIN;

    public BuildingPhaseButtonPresenter(Screen screen, IProvideContext context, PlayerId ourId) {
        this.screen = screen;
        this.ourId = ourId;
        this.context = context;
        toolTips = new CopyOnWriteArrayList<>();
        assignSidebar();
        this.terrainButtons = new ArrayList<>();
        populateTerrainButtons();
        this.creatureButtons = new ArrayList<>();
        populateCreatureButtons();
        this.trapButtons = new ArrayList<>();
        populateTrapButtons();
        this.giftButton = new Button("Gift", Main.TEXT_STATUS);
        setupGiftButton();
        this.currentBuildConsumer = null;
        InputController.get().onMoved(this::lockOnOver, GameScreen.NAME);
        updatePositions();
        Game.window().onResolutionChanged(r -> updatePositions());
    }

    private void setupGiftButton() {
        this.giftButton.setLocation(Main.INNER_MARGIN, Game.window().getHeight() - this.giftButton.getHeight() - 50d);
        screen.getComponents().add(this.giftButton);
        this.giftButton.setEnabled(summonedCreature);
        this.giftButton.onClicked(c -> {
            resetSelection();
            currentBuildConsumer.accept(new BuildChoice(currentTerrain, null, null, null, true));
        });
        this.giftButton.prepare();
    }

    private void assignSidebar() {
        sidebar = Resources.images().get(
                this.ourId == null ? "ui/sidebar_player1.png" : this.ourId.ifOne("ui/sidebar_player1.png",
                                                                                 "ui/sidebar_player2.png"));
    }

    private void lockOnOver(MouseEvent me) {
        if (enabled && me.getX() <= sidebar.getWidth()) {
            context.getBoard().doNotHover();
        } else {
            context.getBoard().doHover();
        }
    }

    private void populateTerrainButtons() {
        // Populate TerrainButtons
        TerrainId[] terrains = TerrainId.values();
        for (var i = 0; i < terrains.length; i++) {
            TerrainId t = terrains[i];
            if (t == TerrainId.T_EMPTY)
                continue;

            final var imgBt = new ImageButton(70d, 70d, Main.INNER_MARGIN, i * 80d - 5d,
                                              t.getImprint().getRenderer().getImage(), Integer.toString(t.getCost()),
                                              Main.TEXT_NORMAL);
            toolTips.add(new ToolTip<>(imgBt, "Place: " + t.getName() + "\nCost: " + t.getCost(), Color.ORANGE));
            imgBt.setEnabledSupplier(() -> t.getCost() <= context.getMoneyLeft());
            imgBt.setFont(Main.GUI_FONT_SMALL);
            terrainButtons.add(imgBt);
            imgBt.onClicked(c -> {
                resetSelection();
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

    private void populateCreatureButtons() {
        // Populate TerrainButtons
        CreatureId[] creatures = CreatureId.values();
        for (var i = 0; i < creatures.length; i++) {
            CreatureId c = creatures[i];
            if (c == CreatureId.NONE)
                continue;
            IRenderData creatureRenderer = c.getRenderer(ourId);
            final var imgBt = new ImageButton(75d, 75d, Main.INNER_MARGIN + 85, i * 80d - 5d,
                                              creatureRenderer.getImage(), Integer.toString(c.getCost()),
                                              Main.TEXT_NORMAL);
            // TODO: skills aswell
            toolTips.add(new ToolTip<>(imgBt, "Summon: " + c.getName() + "\nCost: " + c.getCost(), Color.GREEN));
            imgBt.setEnabledSupplier(() -> c.getCost() <= context.getMoneyLeft());
            imgBt.setFont(Main.GUI_FONT_SMALL);
            creatureButtons.add(imgBt);
            imgBt.onClicked(mce -> {
                resetSelection();
                this.currentCreature = (n, t) -> context.getFactory().getSpell(c).summon(c + "_" + n, t, ourId, true);
                this.currentCreatureId = c;
                this.selectionMode = BuildingPhaseSelectionMode.CREATURE;

                final var img = context.getFactory().getScaledImage(c, ourId.ifOne(true, false));
                if (img != null) {
                    Game.window().cursor().setVisible(true);
                    Game.window().cursor().set(img);
                    context.getBoard().setHighlightMask(SimpleHighlighter.get());
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

    private void populateTrapButtons() {
        // Populate TerrainButtons
        TrapId[] traps = TrapId.values();
        for (var i = 0; i < traps.length; i++) {
            TrapId t = traps[i];
            if (!t.canBeBuildByPlayer())
                continue;
            IRenderData trapRenderer = t.getImprint().getNormalRenderer();
            final var imgBt = new ImageButton(75d, 75d, Main.INNER_MARGIN + 85,
                                              Game.window().getHeight() - i * 80d - 110d,
                                              trapRenderer.getImage(), Integer.toString(t.getCost()),
                                              Main.TEXT_NORMAL);
            toolTips.add(new ToolTip<>(imgBt, "Spawn: " + t.getName() + "\nCost: " + t.getCost(), Color.MAGENTA));
            imgBt.setEnabledSupplier(() -> t.getCost() <= context.getMoneyLeft());
            imgBt.setFont(Main.GUI_FONT_SMALL);
            trapButtons.add(imgBt);
            imgBt.onClicked(mce -> {
                resetSelection();
                this.currentTrapId = t;
                this.selectionMode = BuildingPhaseSelectionMode.TRAP;

                TrapImprint imprint = t.getImprint();
                Game.window().cursor().setVisible(true);
                Game.window().cursor().set(t.getImprint().getNormalRenderer().getImageScaled());
                context.getBoard().setHighlightMask(new ImprintHighlighter(imprint));
                Game.window().cursor().showDefaultCursor();
            });
            screen.getComponents().add(imgBt);
            imgBt.prepare();
            imgBt.setTextAlign(Align.CENTER);
        }
    }

    private void updatePositions() {
        for (var i = 0; i < trapButtons.size(); i++) {
            trapButtons.get(i).setLocation(Main.INNER_MARGIN + 90, Game.window().getHeight() - i * 80d - 110d);
        }
    }

    private void resetSelection() {
        this.currentCreature = null;
        this.currentCreatureId = null;
        this.currentTerrain = null;
        this.currentTrapId = null;
    }

    public void disable() {
        terrainButtons.forEach(GuiComponent::suspend);
        creatureButtons.forEach(GuiComponent::suspend);
        trapButtons.forEach(GuiComponent::suspend);
        this.giftButton.suspend();
        screen.getComponents().removeAll(terrainButtons);
        screen.getComponents().removeAll(creatureButtons);
        screen.getComponents().removeAll(trapButtons);
        resetSelection();
        enabled = false;
        resetCursor();
    }

    public void enable() {
        terrainButtons.forEach(ImageButton::prepare);
        creatureButtons.forEach(ImageButton::prepare);
        trapButtons.forEach(ImageButton::prepare);
        this.giftButton.setEnabled(summonedCreature);
        this.giftButton.prepare();
        screen.getComponents().addAll(terrainButtons);
        screen.getComponents().addAll(creatureButtons);
        screen.getComponents().addAll(trapButtons);
        enabled = true;
    }

    private void resetCursor() {
        Game.window().cursor().set(Main.DEFAULT_CURSOR);
        context.getBoard().setHighlightMask(SimpleHighlighter.get());
    }

    public void setCurrentBuildConsumer(Consumer<BuildChoice> currentBuildConsumer) {
        this.currentBuildConsumer = currentBuildConsumer;
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

    private boolean intersectsWithSideBar(Point p) {
        return enabled && p.getX() <= sidebar.getWidth();
    }

    private void plantTile(MouseEvent c) {
        if (this.selectionMode != BuildingPhaseSelectionMode.TERRAIN || context.getArchitect() == null) {
            return;
        }

        if (currentTerrain == null || currentTerrain == TerrainId.T_EMPTY)
            return;

        if (c.getButton() != MouseEvent.BUTTON1)
            return;

        final var p = c.getPoint();
        if (intersectsWithSideBar(p))
            return;
        final var t = context.getBoard().findTile(p);
        if (t == null)
            return;

        if (context.getArchitect().placeImprint(t.getCoordinate(), currentTerrain.getImprint())
                && currentBuildConsumer != null)
            currentBuildConsumer.accept(new BuildChoice(currentTerrain, null, null, t.getCoordinate(), false));
    }

    private void spawnTrap(MouseEvent c) {
        if (this.selectionMode != BuildingPhaseSelectionMode.TRAP || context.getArchitect() == null) {
            return;
        }

        if (currentTrapId == null)
            return;

        if (c.getButton() != MouseEvent.BUTTON1)
            return;

        final var p = c.getPoint();
        if (intersectsWithSideBar(p))
            return;
        final var t = context.getBoard().findTile(p);
        if (t == null)
            return;

        if ((c.getButton() == MouseEvent.BUTTON1 || c.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) && trapCanBePlacedHere(
                t)) {
            context.getSpawner().spawnTrap(currentTrapId, ourId, t);
            currentBuildConsumer.accept(new BuildChoice(null, null, currentTrapId, t.getCoordinate(), false));
        }
    }

    private boolean trapCanBePlacedHere(final Tile t) {
        return t.getTerrainType() == TerrainTile.EMPTY && context.getFactory().get(t.getCoordinate()).isEmpty()
                && context.getSpawner().canBePlaced(context.getFactory(), currentTrapId, t, ourId,
                                                    context.getBoard());
    }


    private void summonCreature(MouseEvent c) {
        if (this.selectionMode != BuildingPhaseSelectionMode.CREATURE || context.getArchitect() == null) {
            return;
        }

        if (currentCreature == null)
            return;

        if (c.getButton() != MouseEvent.BUTTON1)
            return;


        final var p = c.getPoint();
        if (intersectsWithSideBar(p))
            return;
        final var t = context.getBoard().findTile(p);
        if (t == null)
            return;
        if ((c.getButton() == MouseEvent.BUTTON1 || c.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) && creatureCanBeSummonedHere(
                t)) {
            currentCreature.summon(UUID.randomUUID().toString(), t);
            summonedCreature = true;
            currentBuildConsumer.accept(new BuildChoice(null, currentCreatureId, null, t.getCoordinate(), false));
        }
    }

    private boolean creatureCanBeSummonedHere(final Tile t) {
        return t.getTerrainType().canBeWalkedOn() && (ourId == t.getPlacementOwner()) && context.getTraps().get(
                t.getCoordinate()).isEmpty()
                && context.getFactory().get(t.getCoordinate()).isEmpty();
    }

    @Override
    public void render(Graphics2D g) {
        if (!enabled)
            return;
        ImageRenderer.render(g, sidebar, 0, 0);
        g.setColor(Color.WHITE);
        g.setFont(Main.TEXT_STATUS);
        final var money = Integer.toString(context.getMoneyLeft());
        TextRenderer.render(g, money, 110d, 43d - TextRenderer.getHeight(g, money) / 2d);
        ImageRenderer.render(g, MONEY_SYMBOL, 40d + MONEY_SYMBOL.getWidth() / 2d,
                             43d - MONEY_SYMBOL.getHeight() / 2d - TextRenderer.getHeight(g, money) / 2 - 5d);
    }

    public List<ToolTip<?>> getToolTips() {
        return toolTips;
    }

    public void reset() {
        disable();
    }

    private enum BuildingPhaseSelectionMode {
        TERRAIN, CREATURE, TRAP
    }
}

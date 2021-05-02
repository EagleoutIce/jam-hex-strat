package de.flojo.jam.game.creature.skills;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.CreatureAttributes;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.creature.IAction;
import de.flojo.jam.game.creature.controller.CreatureActionController;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.graphics.Button;
import de.flojo.jam.graphics.ISingleActionPresenter;
import de.flojo.jam.graphics.SingleMovePresenter;
import de.flojo.jam.graphics.SingleSkillPresenter;
import de.flojo.jam.graphics.SingleSkipPresenter;
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.InputController;
import de.flojo.jam.util.ToolTip;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class SkillsPresenter {
    private final Screen target;
    private final Board board;
    private final CreatureFactory factory;
    private final CreatureActionController actionController;
    private final AtomicBoolean enabled = new AtomicBoolean();
    private final Map<AbstractSkill, GuiComponent> skillPresenters;
    private final List<BoardCoordinate> movementBuffer;
    private PlayerId playerId;
    private Creature currentCreature;
    private BoardCoordinate creatureCoordinate;
    private GuiComponent movementSkill;
    private GuiComponent skipSkill;
    private IAction onAction;
    private SingleMovePresenter movementPresenter;
    private List<ToolTip<GuiComponent>> toolTips;

    public SkillsPresenter(Screen target, Board board, CreatureFactory factory, TrapSpawner traps, PlayerId playerId,
                           String screenName) {
        this.target = target;
        this.board = board;
        this.factory = factory;
        this.playerId = playerId;
        this.factory.setOnSelectionChanged(this::updateCreature);
        this.skillPresenters = new LinkedHashMap<>();
        this.movementBuffer = new ArrayList<>();
        actionController = new CreatureActionController(
                new DefaultReadContext(board, factory.getCreatures(), traps.getTraps()), screenName);
        Game.window().onResolutionChanged(r -> updatePositions());
        InputController.get().onMoved(this::lockOnMoved, screenName);
    }

    public void setPlayerId(PlayerId playerId) {
        this.playerId = playerId;
    }

    private void updateCreature(Creature c) {
        if (c == null || !enabled.get()) {
            HexStratLogger.log().log(Level.INFO, "Deny for null or disabled ({0})", c);
            return;
        }

        if (c == currentCreature) {
            HexStratLogger.log().log(Level.INFO, "Deny for current creature ({0})", c);
            return;
        }

        if (!movementBuffer.isEmpty())
            moveOperationEnded(currentCreature, false, new BoardCoordinate(-1, -1));

        resetButtons();

        if (this.playerId != null && c.getOwner() != this.playerId) {
            HexStratLogger.log().log(Level.INFO, "Deny for unmet player lock ({0}; {1}; {2})",
                                     new Object[]{c, this.playerId, c.getOwner()});
            return;
        }

        currentCreature = c;
        currentCreature.getCreatureId().getSoundPool().play();
        creatureCoordinate = new BoardCoordinate(currentCreature.getCoordinate());
        currentCreature.highlight();
        setupMoveButton(c);
        setupSkipButton();
        setupSkillButtons(c);

        currentCreature.setOnDead(this::resetButtons);

        target.getComponents().add(movementSkill);
        target.getComponents().add(skipSkill);
        target.getComponents().addAll(skillPresenters.values());
        updatePositions();
    }

    private void setupSkillButtons(Creature c) {
        for (AbstractSkill skill : c.getAttributes().getSkills()) {
            GuiComponent component = setupSkillButton(c, skill);
            toolTips.add(new ToolTip<>(component,
                                       () -> skill.getNameWithFallback() + "\n" + skill.getDescription() + "\nCost: " + skill.getCost() + "\nRange: " + skill.getMinRange() + "-" + (skill.getMaxRange() == Integer.MAX_VALUE ? "unbound" : skill.getMaxRange()) + " Tile(s)\nEffect: " + skill.getMaximumEffectLength() + " Tile(s)"));
            component.prepare();
            skillPresenters.put(skill, component);
        }
    }

    private GuiComponent setupSkillButton(Creature c, AbstractSkill skill) {
        SingleSkillPresenter presenter = ISingleActionPresenter.producePresenter(skill, c);
        GuiComponent presenterComponent = presenter.get();
        presenterComponent.setEnabled(c.canCastSkill(skill));
        presenterComponent.onClicked(me -> {
            actionController.cancelCurrentOperation();
            if (currentCreature == null)
                return;
            currentCreature.setOnDead(this::resetButtons);
            if (actionController.requestSkillFor(currentCreature, skill,
                                                 (p, t) -> skillOperationEnded(c, presenter, p, t))) {
                HexStratLogger.log().log(Level.INFO, "Skill-Request for: {0} has been initiated.", currentCreature);
                presenterComponent.setEnabled(false);
                skipSkill.setEnabled(false);
            }
        });
        presenter.update(c);
        return presenterComponent;
    }

    private void setupSkipButton() {
        skipSkill = new SingleSkipPresenter().get();
        toolTips.add(new ToolTip<>(skipSkill,
                                   "Skip your current turn.\nThis will set AP and MP to 0 for the current round."));
        skipSkill.onClicked(me -> {
            actionController.cancelCurrentOperation();
            if (currentCreature != null) {
                currentCreature.skip();
                currentCreature.setOnDead(this::resetButtons);
                if (onAction != null)
                    onAction.onSkip(currentCreature.getCoordinate());
            }
            updatePositions();
        });
        skipSkill.prepare();
    }

    private void setupMoveButton(Creature c) {
        movementPresenter = new SingleMovePresenter(c);
        movementSkill = movementPresenter.get();
        toolTips.add(
                new ToolTip<>(movementSkill, () -> "Move one Field\n" + c.getAttributes().getMpLeft() + "MP left"));
        movementSkill.onClicked(me -> {
            actionController.cancelCurrentOperation();
            if (currentCreature != null)
                currentCreature.setOnDead(() -> {
                    moveOperationEnded(c, false, new BoardCoordinate(-1, -1));
                    resetButtons();
                });
            if (actionController.requestMoveFor(currentCreature, (p, t) -> moveOperationEnded(c, p, t))) {
                HexStratLogger.log().log(Level.INFO, "Movement-Request for: {0} has been initiated.", currentCreature);
                movementSkill.setEnabled(false);
                skipSkill.setEnabled(false);
            }
        });
        movementSkill.prepare();
    }

    private void skillOperationEnded(Creature c, SingleSkillPresenter presenter, Boolean performed,
                                     BoardCoordinate target) {
        if (skipSkill != null)
            skipSkill.setEnabled(true);
        presenter.update(c);
        if (Boolean.TRUE.equals(performed)) {
            c.getAttributes().useAp(presenter.getSkill().getCost());
            if (onAction != null)
                onAction.onSkill(actionController.getActiveStartCoordinate(), target, presenter.getSkill());
        }
        updateSkillButtonStates(c);
    }

    private void updateSkillButtonStates(Creature c) {
        for (Map.Entry<AbstractSkill, GuiComponent> gcPair : skillPresenters.entrySet()) {
            if (gcPair.getValue() != null) {
                gcPair.getValue().setEnabled(c.canCastSkill(gcPair.getKey()));
                if (gcPair.getValue() instanceof Button)
                    gcPair.getValue().setText(gcPair.getKey().getNameWithFallback());
            }
        }
    }

    private void moveOperationEnded(Creature c, Boolean performed, BoardCoordinate target) {
        if (skipSkill != null)
            skipSkill.setEnabled(true);
        if (c == null) {
            movementBuffer.clear();
            return;
        }

        final CreatureAttributes attributes = c.getAttributes();
        if (Boolean.TRUE.equals(performed)) {
            attributes.useMp();
            movementPresenter.update(c);
            movementBuffer.add(target);
        }

        if ((!performed || attributes.getMpLeft() <= 0) && !movementBuffer.isEmpty()
                && onAction != null) {
            onAction.onMove(creatureCoordinate, movementBuffer);
            movementBuffer.clear();
        }

        updateSkillButtonStates(c);

        if (movementSkill != null)
            movementSkill.setEnabled(attributes.getMpLeft() > 0);
    }

    private void lockOnMoved(MouseEvent mm) {
        if (notActive())
            return;
        if (intersectsWithButton(mm.getPoint())) {
            board.doNotHover();
        } else {
            board.doHover();
        }
    }

    private boolean intersectsWithButton(Point p) {
        // this did escalate... maybe with list?
        if (intersectsWith(movementSkill, p) || intersectsWith(skipSkill, p)) {
            return true;
        }
        for (GuiComponent component : skillPresenters.values()) {
            if (intersectsWith(component, p))
                return true;
        }
        return false;
    }

    private boolean intersectsWith(GuiComponent g, Point p) {
        return g != null && g.getBoundingBox().contains(p);
    }

    private void updatePositions() {
        if (notActive())
            return;
        CreatureAttributes attributes = currentCreature.getAttributes();

        movementSkill.setEnabled(attributes.getMpLeft() > 0);
        movementPresenter.update(currentCreature);
        if (movementBuffer.isEmpty())
            skipSkill.setEnabled(attributes.canDoSomething());
        int width = Game.window().getWidth();
        int height = Game.window().getHeight();
        double mw = movementSkill.getWidth();
        double offCounter = Main.INNER_MARGIN + mw + 10;
        skipSkill.setLocation(width - offCounter, height - skipSkill.getHeight() - 60d);
        movementSkill.setLocation(width - offCounter - skipSkill.getWidth() - 10,
                                  height - movementSkill.getHeight() - 60d);
        offCounter += skipSkill.getWidth() + 10;
        for (Map.Entry<AbstractSkill, GuiComponent> skillPair : skillPresenters.entrySet()) {
            final GuiComponent component = skillPair.getValue();
            component.setEnabled(currentCreature.canCastSkill(skillPair.getKey()));
            offCounter += component.getWidth() + 10;
            component.setLocation(width - offCounter - 10, height - 60d - component.getHeight());
        }

    }

    private boolean notActive() {
        return currentCreature == null || !enabled.get();
    }

    private void resetButtons() {
        toolTips = new CopyOnWriteArrayList<>();
        skillPresenters.values().forEach(GuiComponent::suspend);
        target.getComponents().removeAll(skillPresenters.values());
        if (movementSkill != null)
            movementSkill.suspend();
        if (skipSkill != null)
            skipSkill.suspend();
        target.getComponents().remove(movementSkill);
        target.getComponents().remove(skipSkill);
        skillPresenters.clear();
        movementSkill = null;
        skipSkill = null;
        if (currentCreature != null) {
            updateSkillButtonStates(currentCreature);
            // reset
            currentCreature.unsetHighlight();
            currentCreature.setOnDead(null);
            currentCreature = null;
        }
        creatureCoordinate = null;
    }

    public void enable() {
        enabled.set(true);
        updateCreature(factory.getSelectedCreature());
    }

    public void reset() {
        disable();
        this.onAction = null;
        this.setPlayerId(null);
    }

    public void disable() {
        enabled.set(false);
        if (currentCreature != null)
            currentCreature.unsetHighlight();
        currentCreature = null;
        creatureCoordinate = null;
        movementBuffer.clear();
        resetButtons();
    }

    public Screen getTarget() {
        return target;
    }

    public void update() {
        updatePositions();
    }

    public void setOnActionConsumer(IAction onAction) {
        this.onAction = onAction;
    }

    public List<ToolTip<GuiComponent>> getToolTips() {
        return toolTips;
    }
}

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
import de.flojo.jam.graphics.SingleSkillPresenter;
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class SkillsPresenter {

    private final Screen target;
    private final Board board;
    private final CreatureFactory factory;
    private final TrapSpawner traps;
    private final CreatureActionController actionController;
    private final AtomicBoolean enabled = new AtomicBoolean();
    private final Map<AbstractSkill, GuiComponent> skillPresenter;
    private final List<BoardCoordinate> movementBuffer;
    private PlayerId playerId;
    private Creature currentCreature;
    private BoardCoordinate creatureCoordinate;
    private Button moveButton;
    private Button skipButton;
    private IAction onAction;

    public SkillsPresenter(Screen target, Board board, CreatureFactory factory, TrapSpawner traps, PlayerId playerId,
                           String screenName) {
        this.target = target;
        this.board = board;
        this.factory = factory;
        this.traps = traps;
        this.playerId = playerId;
        this.factory.setOnSelectionChanged(this::updateCreature);
        this.skillPresenter = new LinkedHashMap<>();
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

        if (!movementBuffer.isEmpty()) {
            // HexStratLogger.log().log(Level.INFO, "Deny for unfinished move buffer ({0}; {1})", new Object[] {c, movementBuffer});
            moveOperationEnded(currentCreature, false, new BoardCoordinate(-1, -1));
        }

        resetButtons();

        if (this.playerId != null && c.getOwner() != this.playerId) {
            HexStratLogger.log().log(Level.INFO, "Deny for unmet player lock ({0}; {1}; {2})", new Object[]{c, this.playerId, c.getOwner()});
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

        target.getComponents().add(moveButton);
        target.getComponents().add(skipButton);
        target.getComponents().addAll(skillPresenter.values());
        updatePositions();
    }

    private void setupSkillButtons(Creature c) {
        for (AbstractSkill skill : c.getAttributes().getSkills()) {
            GuiComponent component = setupSkillButton(c, skill);
            component.prepare();
            skillPresenter.put(skill, component);
        }
    }

    private GuiComponent setupSkillButton(Creature c, AbstractSkill skill) {
        SingleSkillPresenter presenter = ISingleActionPresenter.producePresenter(skill);
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
                skipButton.setEnabled(false);
            }
        });
        return presenterComponent;
    }

    private void setupSkipButton() {
        skipButton = new Button("Skip", Main.GUI_FONT_SMALL);
        skipButton.onClicked(me -> {
            actionController.cancelCurrentOperation();
            currentCreature.skip();
            currentCreature.setOnDead(this::resetButtons);
            if (onAction != null)
                onAction.onSkip(currentCreature.getCoordinate());
            updatePositions();
        });
        skipButton.prepare();
    }

    private void setupMoveButton(Creature c) {
        moveButton = new Button("Move", Main.GUI_FONT_SMALL);
        moveButton.onClicked(me -> {
            actionController.cancelCurrentOperation();
            if (currentCreature != null)
                currentCreature.setOnDead(() -> {
                    moveOperationEnded(c, false, new BoardCoordinate(-1, -1));
                    resetButtons();
                });
            if (actionController.requestMoveFor(currentCreature, (p, t) -> moveOperationEnded(c, p, t))) {
                HexStratLogger.log().log(Level.INFO, "Movement-Request for: {0} has been initiated.", currentCreature);
                moveButton.setEnabled(false);
                skipButton.setEnabled(false);
            }
        });
        moveButton.prepare();
    }

    private void skillOperationEnded(Creature c, SingleSkillPresenter presenter, Boolean performed, BoardCoordinate target) {
        if (skipButton != null)
            skipButton.setEnabled(true);
        if (Boolean.TRUE.equals(performed)) {
            c.getAttributes().useAp();
            if (onAction != null)
                onAction.onSkill(actionController.getActiveCreature().getCoordinate(), target, presenter.getSkill());
        }
        updateSkillButtonStates(c);
    }

    private void updateSkillButtonStates(Creature c) {
        for (Map.Entry<AbstractSkill, GuiComponent> gcPair : skillPresenter.entrySet()) {
            if (gcPair.getValue() != null) {
                gcPair.getValue().setEnabled(c.canCastSkill(gcPair.getKey()));
                // TODO: update so that does not do this with a Image Button
                if (gcPair.getValue() instanceof Button)
                    gcPair.getValue().setText(gcPair.getKey().getNameWithFallback());
            }
        }
    }

    private void moveOperationEnded(Creature c, Boolean performed, BoardCoordinate target) {
        if (skipButton != null)
            skipButton.setEnabled(true);
        if (c == null) {
            movementBuffer.clear();
            return;
        }

        final CreatureAttributes attributes = c.getAttributes();
        if (Boolean.TRUE.equals(performed)) {
            attributes.useMp();
            movementBuffer.add(target);
        }

        if ((!performed || attributes.getMpLeft() <= 0) && !movementBuffer.isEmpty()
                && onAction != null) {
            onAction.onMove(creatureCoordinate, movementBuffer);
            movementBuffer.clear();
        }

        updateSkillButtonStates(c);

        if (moveButton != null)
            moveButton.setEnabled(attributes.getMpLeft() > 0);
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
        if (intersectsWith(moveButton, p) || intersectsWith(skipButton, p)) {
            return true;
        }
        for (GuiComponent component : skillPresenter.values()) {
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
        moveButton.setEnabled(attributes.getMpLeft() > 0);
        if (movementBuffer.isEmpty())
            skipButton.setEnabled(attributes.canDoSomething());
        int width = Game.window().getWidth();
        int height = Game.window().getHeight();
        double mw = moveButton.getWidth();
        double offCounter = Main.INNER_MARGIN + mw;
        moveButton.setLocation(width - offCounter, height - moveButton.getHeight() - 60d);
        for (GuiComponent component : skillPresenter.values()) {
            component.setEnabled(attributes.getApLeft() > 0);
            offCounter += component.getWidth() + 10;
            component.setLocation(width - offCounter, height - 60d - component.getHeight());
        }

        skipButton.setLocation(width - offCounter - skipButton.getWidth() - 10, height - skipButton.getHeight() - 60d);
    }

    private boolean notActive() {
        return currentCreature == null || !enabled.get();
    }

    private void resetButtons() {
        skillPresenter.values().forEach(GuiComponent::suspend);
        target.getComponents().removeAll(skillPresenter.values());
        if (moveButton != null)
            moveButton.suspend();
        if (skipButton != null)
            skipButton.suspend();
        target.getComponents().remove(moveButton);
        target.getComponents().remove(skipButton);
        skillPresenter.clear();
        moveButton = null;
        skipButton = null;
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

    public void disable() {
        enabled.set(false);
        if (currentCreature != null)
            currentCreature.unsetHighlight();
        currentCreature = null;
        creatureCoordinate = null;
        movementBuffer.clear();
        resetButtons();
    }

    public CreatureFactory getFactory() {
        return factory;
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

}

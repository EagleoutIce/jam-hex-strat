package de.flojo.jam.game.creature.skills;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

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
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.gui.screens.Screen;

public class SkillsPresenter {

    private final Screen target;
    private final Board board;
    private final CreatureFactory factory;
    private final TrapSpawner traps;

    private PlayerId playerId;
    private Creature currentCreature;
    private BoardCoordinate creatureCoordinate;

    private AtomicBoolean enabled = new AtomicBoolean();

    private final CreatureActionController actionController;

    private Button moveButton;
    private Button skipButton;
    private List<Button> skillButtons;
    private List<BoardCoordinate> movementBuffer;
    private IAction onAction;

    public SkillsPresenter(Screen target, Board board, CreatureFactory factory, TrapSpawner traps, PlayerId playerId,
            String screenName) {
        this.target = target;
        this.board = board;
        this.factory = factory;
        this.traps = traps;
        this.playerId = playerId;
        this.factory.setOnSelectionChanged(this::updateCreature);
        this.skillButtons = new LinkedList<>();
        this.movementBuffer = new ArrayList<>();
        actionController = new CreatureActionController(
                new DefaultEffectContext(board, factory.getCreatures(), traps.getTraps()), screenName);
        Game.window().onResolutionChanged(r -> updatePositions());
        InputController.get().onMoved(this::lockOnMoved, screenName);
    }

    public void setPlayerId(PlayerId playerId) {
        this.playerId = playerId;
    }

    private void updateCreature(Creature c) {
        if (c == null || !enabled.get()) {
            return;
        }

        if (c == currentCreature)
            return;

        if (!movementBuffer.isEmpty()) {
            moveOperationEnded(currentCreature.getAttributes(), false, new BoardCoordinate(-1, -1));
            return;
        }

        resetButtons();

        if (this.playerId != null && c.getOwner() != this.playerId)
            return;

        currentCreature = c;
        creatureCoordinate = new BoardCoordinate(currentCreature.getCoordinate());
        currentCreature.highlight();
        CreatureAttributes attributes = c.getAttributes();
        moveButton = new Button("Move", Main.GUI_FONT_SMALL);
        moveButton.onClicked(me -> {
            actionController.cancelCurrentOperation();
            currentCreature.setOnDead(() -> {
                moveOperationEnded(attributes, false, new BoardCoordinate(-1, -1));
                resetButtons();
            });
            if (actionController.requestMoveFor(currentCreature, (p, t) -> moveOperationEnded(attributes, p, t))) {
                Game.log().log(Level.INFO, "Movement-Request for: {0} has been initiated.", currentCreature);
                moveButton.setEnabled(false);
                skipButton.setEnabled(false);
            }
        });
        moveButton.prepare();
        skipButton = new Button("Skip", Main.GUI_FONT_SMALL);
        skipButton.onClicked(me -> {
            actionController.cancelCurrentOperation();
            currentCreature.skip();
            currentCreature.setOnDead(this::resetButtons);
            onAction.onSkip(currentCreature.getCoordinate());
            updatePositions();
        });
        skipButton.prepare();

        for (ICreatureSkill skill : attributes.getSkills()) {
            Button bt = new Button(skill.getName(), Main.GUI_FONT_SMALL);
            bt.setEnabled(attributes.getMpLeft() > 0);
            bt.onClicked(me -> {
                actionController.cancelCurrentOperation();
                currentCreature.setOnDead(this::resetButtons);
                if (actionController.requestSkillFor(currentCreature, skill.getSkillId(),
                        (p, t) -> skillOperationEnded(attributes, bt, p, skill.getSkillId(), t))) {
                    Game.log().log(Level.INFO, "Skill-Request for: {0} has been initiated.", currentCreature);
                    bt.setEnabled(false);
                    skipButton.setEnabled(false);
                }
            });
            bt.prepare();
            skillButtons.add(bt);
        }

        currentCreature.setOnDead(this::resetButtons);

        target.getComponents().add(moveButton);
        target.getComponents().add(skipButton);
        target.getComponents().addAll(skillButtons);
        updatePositions();
    }

    private void skillOperationEnded(CreatureAttributes attributes, Button button, Boolean performed, SkillId skillId,
            BoardCoordinate target) {
        if (performed.booleanValue()) {
            attributes.useAp();
            if (onAction != null)
                onAction.onSkill(actionController.getActiveCreature().getCoordinate(), target, skillId);
        }
        if (button != null)
            button.setEnabled(attributes.getApLeft() > 0);
    }

    private void moveOperationEnded(CreatureAttributes attributes, Boolean performed, BoardCoordinate target) {
        if (performed.booleanValue()) {
            attributes.useMp();
            movementBuffer.add(target);
        }

        if ((!performed.booleanValue() || attributes.getMpLeft() <= 0) && !movementBuffer.isEmpty()
                && onAction != null) {
            onAction.onMove(creatureCoordinate, movementBuffer);
            movementBuffer.clear();
        }

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
        for (Button button : skillButtons) {
            if (intersectsWith(button, p))
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
        if(movementBuffer.isEmpty())
            skipButton.setEnabled(attributes.canDoSomething());
        int width = Game.window().getWidth();
        int height = Game.window().getHeight();
        double mw = moveButton.getWidth();
        double offCounter = Main.INNER_MARGIN + mw;
        moveButton.setLocation(width - offCounter, height - 90d);
        for (Button button : skillButtons) {
            button.setEnabled(attributes.getApLeft() > 0);
            offCounter += button.getWidth() + 10;
            button.setLocation(width - offCounter, height - 90d);
        }

        skipButton.setLocation(width - offCounter - skipButton.getWidth() - 10, height - 90d);
    }

    private boolean notActive() {
        return currentCreature == null || !enabled.get();
    }

    private void resetButtons() {
        skillButtons.forEach(GuiComponent::suspend);
        target.getComponents().removeAll(skillButtons);
        if (moveButton != null)
            moveButton.suspend();
        if (skipButton != null)
            skipButton.suspend();
        target.getComponents().remove(moveButton);
        target.getComponents().remove(skipButton);
        skillButtons.clear();
        moveButton = null;
        skipButton = null;
        if (currentCreature != null) {
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
        if(currentCreature != null)
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

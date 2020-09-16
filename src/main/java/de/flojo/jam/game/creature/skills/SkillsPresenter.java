package de.flojo.jam.game.creature.skills;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import de.flojo.jam.Main;
import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.CreatureAttributes;
import de.flojo.jam.game.creature.CreatureFactory;
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

    private boolean skillChoosing = false;

    private PlayerId playerId;
    private Creature currentCreature;

    private AtomicBoolean enabled = new AtomicBoolean();

    private final CreatureActionController actionController;

    Button moveButton;
    private List<Button> skillButtons;

    public SkillsPresenter(Screen target, Board board, CreatureFactory factory, TrapSpawner traps, PlayerId playerId,
            String screenName) {
        this.target = target;
        this.board = board;
        this.factory = factory;
        this.traps = traps;
        this.playerId = playerId;
        this.factory.setOnSelectionChanged(this::updateCreature);
        skillButtons = new LinkedList<>();
        actionController = new CreatureActionController(
                new DefaultEffectContext(board, factory.getCreatures(), traps.getTraps()), screenName);
        Game.window().onResolutionChanged(r -> updatePositions());
        InputController.get().onMoved(this::lockOnMoved, screenName);
    }

    // TODO: if figure dies remove tooo

    public void setPlayerId(PlayerId playerId) {
        this.playerId = playerId;
    }

    private void updateCreature(Creature c) {
        if (c == null || !enabled.get()) {
            return;
        }
        if(skillChoosing) {
            Game.log().log(Level.INFO, "Ignored character change for: {0} as the skill target choosing menu is active.", c);
            return;
        }

        resetButtons();

        if (this.playerId != null && c.getOwner() != this.playerId)
            return;

        currentCreature = c;
        currentCreature.highlight();
        CreatureAttributes attributes = c.getAttributes();
        moveButton = new Button("Move", Main.GUI_FONT_SMALL);
        moveButton.onClicked(me -> {
            actionController.cancelCurrentOperation();
            if(actionController.requestMoveFor(currentCreature, p -> moveOperationEnded(attributes, p))) {
                Game.log().log(Level.INFO, "Movement-Request for: {0} has been initiated.", currentCreature);
                moveButton.setEnabled(false);
            }
        });
        moveButton.prepare();

        for (ICreatureSkill skill : attributes.getSkills()) {
            Button bt = new Button(skill.getName(), Main.GUI_FONT_SMALL);
            bt.setEnabled(attributes.getMpLeft() > 0);
            bt.onClicked(me -> {
                actionController.cancelCurrentOperation();
                if(actionController.requestSkillFor(currentCreature, skill.getSkillId(),  p -> skillOperationEnded(attributes, bt, p) )) {
                    Game.log().log(Level.INFO, "Skill-Request for: {0} has been initiated.", currentCreature);
                    bt.setEnabled(false);
                }
            });
            bt.prepare();
            skillButtons.add(bt);
        }

        currentCreature.setOnDead(this::resetButtons);

        // TODO: onClick

        target.getComponents().add(moveButton);
        target.getComponents().addAll(skillButtons);
        updatePositions();
    }

    private void skillOperationEnded(CreatureAttributes attributes, Button button, Boolean performed) {
        if(performed.booleanValue())
            attributes.useAp();
        if(button != null) {
            button.setEnabled(attributes.getApLeft() > 0);
        }
    }

    private void moveOperationEnded(CreatureAttributes attributes, Boolean performed) {
        if(performed.booleanValue())
            attributes.useMp();
        if(moveButton != null) {
            moveButton.setEnabled(attributes.getMpLeft() > 0);
        }
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
        if (moveButton.getBoundingBox().contains(p)) {
            return true;
        }
        for (Button button : skillButtons) {
            if (button.getBoundingBox().contains(p))
                return true;
        }
        return false;
    }

    private void updatePositions() {
        if (notActive())
            return;
        CreatureAttributes attributes = currentCreature.getAttributes();
        moveButton.setEnabled(attributes.getMpLeft() > 0);
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
    }

    private boolean notActive() {
        return currentCreature == null || !enabled.get();
    }

    private void resetButtons() {
        skillButtons.forEach(GuiComponent::suspend);
        target.getComponents().removeAll(skillButtons);
        if (moveButton != null)
            moveButton.suspend();
        target.getComponents().remove(moveButton);
        skillButtons.clear();
        moveButton = null;
        if (currentCreature != null) {
            // reset
            currentCreature.unsetHighlight();
            currentCreature.setOnDead(null);
            currentCreature = null;
        }
    }

    public void enable() {
        enabled.set(true);
        currentCreature = factory.getSelectedCreature();
    }

    public void disable() {
        enabled.set(false);
        currentCreature = null;
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

}

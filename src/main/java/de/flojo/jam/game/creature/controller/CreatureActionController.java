package de.flojo.jam.game.creature.controller;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.Trap;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.AbstractSkill;
import de.flojo.jam.game.creature.skills.CreatureSkillAOAGenerator;
import de.flojo.jam.game.creature.skills.IProvideReadContext;
import de.flojo.jam.game.creature.skills.JsonDataOfSkill;
import de.flojo.jam.game.creature.skills.TargetOfSkill;
import de.flojo.jam.util.HexStratLogger;
import de.flojo.jam.util.InputController;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public class CreatureActionController {

    private final IProvideReadContext context;
    private final String screenName;

    private final Object selectionLock = new Object();
    private final Set<Tile> possibleTargets;
    private Creature activeCreature = null;
    private boolean completed = false;
    private boolean performed = false;
    private BiConsumer<Boolean, BoardCoordinate> onCompleted = null;
    private AbstractSkill currentSkill = null;
    private BoardCoordinate clickedOn;
    private CurrentActionType currentActionType = CurrentActionType.MOVEMENT;

    public CreatureActionController(IProvideReadContext context, final String screenName) {
        this.context = context;
        this.screenName = screenName;
        this.possibleTargets = new HashSet<>();
        InputController.get().onClicked(this::onClickPerform, screenName);
    }

    public static void awaitMovementCompleteAsync(Creature target, Runnable onCompleted) {
        Thread thread = new Thread(() -> {
            awaitMovementComplete(target);
            onCompleted.run();
        });
        thread.start();
    }

    public static void awaitMovementComplete(Creature target) {
        awaitMovementComplete(target, 20);

    }

    public static void awaitMovementComplete(Creature target, int msTimeout) {
        synchronized (target.moveLock()) {
            while (!target.getBase().moveTargetIsReached()) {
                try {
                    target.moveLock().wait(msTimeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static boolean processMovement(TrapCollection traps, Creature creature, Tile tile) {
        Optional<Trap> mayTrap = traps.get(tile.getCoordinate());
        creature.move(tile);
        if (mayTrap.isPresent() && !creature.isFlying()) {
            triggerTrap(creature, mayTrap.get());
            return true; // runs in trap
        }
        return false;
    }

    private static void triggerTrap(Creature creature, Trap trap) {
        creature.moribund();
        CreatureActionController.awaitMovementCompleteAsync(creature, () -> {
            trap.trigger();
            sleep(trap.getAnimationCooldown());
            creature.die();
        });
    }

    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public static boolean processMovementBlocking(TrapCollection traps, Creature creature, Tile tile) {
        Optional<Trap> mayTrap = traps.get(tile.getCoordinate());
        creature.moveBlocking(tile);
        if (mayTrap.isPresent() && !creature.isFlying()) {
            triggerTrap(creature, mayTrap.get());
            return true; // runs in trap
        }
        return false;
    }

    private boolean isWalkable(Tile t) {
        return t.getTerrainType().canBeWalkedOn() && context.getCreatures().get(t.getCoordinate()).isEmpty();
    }

    public boolean requestSkillFor(Creature creature, JsonDataOfSkill skillData,
                                   BiConsumer<Boolean, BoardCoordinate> onCompleted) {
        if (activeCreature != null) {
            HexStratLogger.log().log(Level.WARNING,
                                     "Ignored Operation-Skill ({2}) request for {0} as there was anther active Creature ({1})",
                                     new Object[]{creature, activeCreature, skillData});
            return false;
        }

        currentActionType = CurrentActionType.SKILL;

        if (creature.getAttributes().getApLeft() <= 0)
            return false;

        Optional<AbstractSkill> mayBeSkill = creature.getSkill(skillData);
        if (mayBeSkill.isEmpty()) {
            HexStratLogger.log().log(Level.WARNING, "Creature {0} does not possess skill {1}",
                                     new Object[]{creature, skillData});
            return false;
        }
        currentSkill = mayBeSkill.get();

        completed = false;
        performed = false;
        this.onCompleted = onCompleted;

        activeCreature = creature;
        possibleTargets.clear();

        Tile start = creature.getBase().getTile();
        possibleTargets.addAll(
                CreatureSkillAOAGenerator.getAOA(currentSkill, start, context.getBoard(), context.getCreatures()));
        possibleTargets.forEach(t -> t.mark(true));
        return true;
    }

    public boolean requestMoveFor(Creature creature, BiConsumer<Boolean, BoardCoordinate> onCompleted) {
        if (activeCreature != null) {
            HexStratLogger.log().log(Level.WARNING,
                                     "Ignored Operation-Movement request for {0} as there was another active Creature ({1})",
                                     new Object[]{creature, activeCreature});
            return false;
        } else if (creature == null) {
            HexStratLogger.log().severe("Requested Operation-Movement for null creature!");
            return false;
        }

        currentActionType = CurrentActionType.MOVEMENT;

        if (creature.getAttributes().getMpLeft() <= 0)
            return false;

        this.onCompleted = onCompleted;

        activeCreature = creature;
        possibleTargets.clear();

        Tile start = creature.getBase().getTile();
        Set<Tile> neighbours = start.getNeighbours();
        for (Tile tile : neighbours) {
            if (isWalkable(tile)) {
                possibleTargets.add(tile);
                tile.mark(true);
            }
        }

        return true;
    }

    private void onClickPerform(MouseEvent me) {
        if (activeCreature == null || completed)
            return;

        if (me.getButton() == MouseEvent.BUTTON3) {
            completed(false);
            return;
        }

        if (me.getButton() != MouseEvent.BUTTON1)
            return;

        Point target = me.getPoint();
        Optional<Tile> mayTile = identifyClickedOnValidTile(target);
        if (mayTile.isEmpty())
            return;

        Tile tile = mayTile.get();
        this.clickedOn = tile.getCoordinate();

        switch (currentActionType) {
            case MOVEMENT:
                performOnClickMovementOn(tile);
                return;
            case SKILL:
                performOnClickSkillOn(tile);
                return;
            default:
                errorOnClickOnUnknownActionType(tile);
        }
    }

    private void errorOnClickOnUnknownActionType(Tile tile) {
        HexStratLogger.log().log(Level.INFO,
                                 "Clicked on: {0} with ({1}). But the current Action type ({2}) has no performer attached.",
                                 new Object[]{tile, activeCreature, currentActionType});
        performed = false;
        completed(false);
    }

    private void performOnClickSkillOn(final Tile tile) {
        Optional<Creature> mayTargetCreature = context.getCreatures().get(tile.getCoordinate());
        Creature targetCreature = null;
        if (mayTargetCreature.isEmpty()) {
            if (currentSkill.getTarget() != TargetOfSkill.TILE) {// perform no tile fun
                if (!currentSkill.isRanged()) {
                    // no creature no valid click
                    return;
                }
                // ELSE TODO: update creature in direction of aoa
            }
        } else {
            targetCreature = mayTargetCreature.get();
        }

        HexStratLogger.log().log(Level.INFO, "Casting Skill {2} on: {0} with ({1})",
                                 new Object[]{tile, activeCreature, currentSkill});
        if (currentSkill.getTarget().equals(TargetOfSkill.TILE)) {
            if (targetCreature != null) {
                HexStratLogger.log().warning("Target Creature not allowed for Tile");
                return;
            }
            activeCreature.useSkill(context, currentSkill, tile);
        } else {
            activeCreature.useSkill(context, currentSkill, targetCreature);
        }

        performed = true;
        completed(false);
    }

    private void performOnClickMovementOn(Tile tile) {
        HexStratLogger.log().log(Level.INFO, "Moving on: {0} with ({1})", new Object[]{tile, activeCreature});
        boolean dies = processMovement(context.getTraps(), activeCreature, tile);
        performed = true;
        completed(!dies);
    }

    private Optional<Tile> identifyClickedOnValidTile(Point target) {
        for (Tile tile : possibleTargets) {
            if (tile.contains(target))
                return Optional.of(tile);
        }
        return Optional.empty();
    }

    public void cancelCurrentOperation() {
        performed = false;
        completed(false);
    }


    private void completed(boolean allowRedoMovement) {
        completed = true;
        synchronized (selectionLock) {
            selectionLock.notifyAll();
        }
        if (onCompleted != null)
            onCompleted.accept(performed, clickedOn);
        // want to move again?
        Creature storedCreature = activeCreature;
        BiConsumer<Boolean, BoardCoordinate> storeOnComplete = onCompleted;
        boolean didPerform = performed;
        reset();
        // redo movement request if mp left
        if (didPerform && allowRedoMovement && storedCreature != null && !requestMoveFor(storedCreature,
                                                                                         storeOnComplete))
            storedCreature.unsetHighlight(); // just to be sure
    }

    void reset() {
        this.activeCreature = null;
        this.clickedOn = null;
        this.possibleTargets.forEach(t -> t.mark(false));
        this.possibleTargets.clear();
        this.onCompleted = null;
        this.completed = false;
        this.performed = false;
        this.currentActionType = CurrentActionType.NONE;
    }

    public Creature getActiveCreature() {
        return activeCreature;
    }

    private enum CurrentActionType {
        MOVEMENT, SKILL, NONE
    }

}

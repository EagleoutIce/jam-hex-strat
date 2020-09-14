package de.flojo.jam.game.creature.controller;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;

import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.traps.Trap;
import de.flojo.jam.game.board.traps.TrapCollection;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.skills.CreatureSkillAOAGenerator;
import de.flojo.jam.game.creature.skills.ICreatureSkill;
import de.flojo.jam.game.creature.skills.IProvideEffectContext;
import de.flojo.jam.game.creature.skills.SkillId;
import de.flojo.jam.util.InputController;
import de.gurkenlabs.litiengine.Game;

public class CreatureActionController {

    private final IProvideEffectContext context;
    private final String screenName;

    private Object selectionLock = new Object();

    private Creature activeCreature = null;
    private Set<Tile> possibleTargets;
    private boolean completed = false;
    private boolean performed = false;
    private Consumer<Boolean> onCompleted = null;
    private ICreatureSkill currentSkill = null;

    private enum CurrentActionType {
        MOVEMENT, SKILL, NONE
    }

    private CurrentActionType currentActionType = CurrentActionType.MOVEMENT;

    public CreatureActionController(IProvideEffectContext context, final String screenName) {
        this.context = context;
        this.screenName = screenName;
        this.possibleTargets = new HashSet<>();
        InputController.get().onClicked(this::onClickPerform, screenName);
    }

    private boolean isWalkable(Tile t) {
        return !t.getTerrainType().blocksWalking() && context.getCreatures().get(t.getCoordinate()).isEmpty();
    }

    public boolean requestSkillFor(Creature creature, SkillId skillId, Consumer<Boolean> onCompleted) {
        if (activeCreature != null) {
            Game.log().log(Level.WARNING, "Ignored Operation-Skill ({2}) request for {0} as there was anther active Creature ({1})", new Object[] {creature, activeCreature, skillId});
            return false;
        }

        currentActionType = CurrentActionType.SKILL;

        if(creature.getAttributes().getApLeft() <= 0)
            return false;

        Optional<ICreatureSkill> mayBeSkill = creature.getSkill(skillId);
        if(mayBeSkill.isEmpty()) {
            Game.log().log(Level.WARNING, "Creature {0} does not posess skill {1}", new Object[] {creature, skillId});
            return false;
        }
        currentSkill = mayBeSkill.get();

        completed = false;
        performed = false;
        this.onCompleted = onCompleted;

        activeCreature = creature;
        possibleTargets.clear();

        Tile start = creature.getBase().getTile();
        possibleTargets.addAll(CreatureSkillAOAGenerator.getAOA(currentSkill, start, context.getBoard(), context.getCreatures()));
        possibleTargets.forEach(t -> t.mark(true));

        return true;
    }


    public boolean requestMoveFor(Creature creature, Consumer<Boolean> onCompleted) {
        if (activeCreature != null) {
            Game.log().log(Level.WARNING, "Ignored Operation-Movement request for {0} as there was anther active Creature ({1})", new Object[] {creature, activeCreature});
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
        Optional<Tile> mayTile = identifyClickedTileOnValid(target);
        if(mayTile.isEmpty())
            return;

        Tile tile = mayTile.get();

        switch(currentActionType) {
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
        Game.log().log(Level.INFO, "Clicked on: {0} with ({1}). But the current Action type ({2}) has no performer attached.", new Object[] { tile, activeCreature, currentActionType});
        performed = false;
        completed(false);
    }


    private void performOnClickSkillOn(final Tile tile) {
        Optional<Creature> mayTargetCreature = context.getCreatures().get(tile.getCoordinate());
        Creature targetCreature;
        if(mayTargetCreature.isEmpty()) {
            if(currentSkill.isRanged()) {
                // TODO: update creature in direction of aoa
                targetCreature = null;
            } else {
                // no creature no valid click
                return;
            }
        } else {
            targetCreature = mayTargetCreature.get();
        }
        
        Game.log().log(Level.INFO, "Casting Skill {2} on: {0} with ({1})", new Object[] { tile, activeCreature, currentSkill });
        activeCreature.useSkill(context, currentSkill, targetCreature);
        performed = true;
        completed(false);
    }

    private void performOnClickMovementOn(Tile tile) {
        Game.log().log(Level.INFO, "Moving on: {0} with ({1})", new Object[] { tile, activeCreature });
        boolean dies = processMovement(context.getTraps(), activeCreature, tile);
        performed = true;
        completed(!dies);
    }

    private Optional<Tile> identifyClickedTileOnValid(Point target) {
        for (Tile tile : possibleTargets) {
            if (tile.contains(target))
                return Optional.of(tile);
        }
        return Optional.empty();
    }

    public static void awaitMovementCompleteAsync(Creature target, Runnable onCompleted) {
        Thread thread = new Thread(() -> {
            awaitMovementComplete(target);
            onCompleted.run();
        });
        thread.start();
    }

    public static void awaitMovementComplete(Creature target) {
        synchronized (target.moveLock()) {
            while (!target.getBase().moveTargetIsReached()) {
                try {
                    target.moveLock().wait();
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
            Trap trap = mayTrap.get();
            CreatureActionController.awaitMovementCompleteAsync(creature, () -> {
                trap.trigger();
                sleep(250);
                creature.die();
            });
            return true; // runs in trap
        }
        return false;
    }

    private static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
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
        if(onCompleted != null)
            onCompleted.accept(performed);
        // want to move again?
        Creature storedCreature = activeCreature;
        Consumer<Boolean> storeOnComplete = onCompleted;
        boolean didPerform = performed;
        reset();
        // redo movement request if mp left
        if(didPerform && allowRedoMovement && storedCreature != null && !requestMoveFor(storedCreature, storeOnComplete))
            storedCreature.unsetHighlight(); // just to be sure
    }

    void reset() {
        this.activeCreature = null;
        this.possibleTargets.forEach(t -> t.mark(false));
        this.possibleTargets.clear();
        this.onCompleted = null;
        this.completed = false;
        this.performed = false;
        this.currentActionType = CurrentActionType.NONE;
    }

}

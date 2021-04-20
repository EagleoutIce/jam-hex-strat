package de.flojo.jam.networking.server.management;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.Tile;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.terrain.management.Terrain;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.creature.CreatureId;
import de.flojo.jam.game.creature.controller.CreatureActionController;
import de.flojo.jam.game.creature.skills.ICreatureSkill;
import de.flojo.jam.game.creature.skills.SkillId;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.messages.GameStartMessage;
import de.flojo.jam.networking.messages.ItIsYourTurnMessage;
import de.flojo.jam.networking.messages.NextRoundMessage;
import de.flojo.jam.networking.messages.TurnActionMessage;
import de.flojo.jam.networking.messages.YouCanBuildMessage;
import de.flojo.jam.networking.server.ClientServerConnection;
import de.flojo.jam.networking.server.PlayerController;
import de.flojo.jam.networking.server.ServerController;
import de.flojo.jam.util.HexStartLogger;
import de.flojo.jam.util.IProvideContext;
import de.gurkenlabs.litiengine.Game;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class MainGameControl {

    private final PlayerController playerController;
    private final ServerController controller;
    private final IProvideContext context;
    private final GameState state;

    public MainGameControl(ServerController controller, IProvideContext context, int startMoney) {
        this.controller = controller;
        this.playerController = controller.getPlayerController();
        this.context = context;
        this.state = new GameState(startMoney);
    }

    public TerrainMap getTerrainMap() {
        return getBoard().getTerrainMap();
    }

    public Terrain getTerrain() {
        return getTerrainMap().getTerrain();
    }

    public void startBuildPhase() {
        // send request message to first player
        requestBuild();
    }

    private void requestBuild() {
        ClientServerConnection currentPlayer = controller.getPlayerController()
                .getPlayer(state.getCurrentTurn());
        currentPlayer.send(new YouCanBuildMessage(null,
                state.getCurrentTurn().ifOne(state.getMoneyP1Left(), state.getMoneyP2Left())));
        HexStartLogger.log().log(Level.INFO, "Sending build request to {0}.", currentPlayer);
    }

    public void buildTerrainAt(PlayerId player, TerrainId terrain, BoardCoordinate position) {
        context.getArchitect().placeImprint(position, terrain.getImprint());
        state.reduceMoney(player, terrain.getCost());
    }

    public boolean nextBuildRequest() {
        if (state.nextBuild()) {
            requestBuild();
            return true;
        }
        return false;
    }

    public void startMainGame() {
        Game.log().info("Started main game!");
        playerController.sendBoth(new GameStartMessage(null, getBoard().getTerrainMap(), getFactory().getCreatures(), getSpawner().getTraps()));
        nextRound();
        nextGameAction();
    }

    private TrapSpawner getSpawner() {
        return context.getSpawner();
    }

    private CreatureFactory getFactory() {
        return context.getFactory();
    }

    private Board getBoard() {
        return context.getBoard();
    }

    public boolean nextGameAction() {
        state.nextPlayer(context.getCreatures());
        if (isGameOver()) {
            // GAME OVER
            Game.log().info("Game Over");
            return false;
        } else if (roundEnd()) {
            nextRound();
        }
        sendTurnRequest();
        return true;
    }

    private void sendTurnRequest() {
        ClientServerConnection currentPlayer = controller.getPlayerController()
                .getPlayer(state.getCurrentTurn());
        currentPlayer.send(new ItIsYourTurnMessage(null));
        HexStartLogger.log().log(Level.INFO, "Sending game-turn request to {0}.", currentPlayer);
    }

    private void nextRound() {
        state.nextRound();
        playerController.sendBoth(new NextRoundMessage(null, state.getCurrentRound()));
        getFactory().resetAll();
    }

    private boolean roundEnd() {
        return getFactory().noneCanDoSomething();
    }

    private boolean isGameOver() {
        return !getFactory().playerOneOwns() || !getFactory().playerTwoOwns();
    }


    public Optional<PlayerId> getWinner() {
        final boolean p1 = getFactory().playerOneOwns();
        final boolean p2 = getFactory().playerTwoOwns();
        if (p1 ^ p2) {
            return p1 ? Optional.of(playerController.getPlayerOne().getRole()) : Optional.of(playerController.getPlayerTwo().getRole());
        } else {
            return Optional.empty();
        }
    }


    public void summonCreatureAt(PlayerId player, CreatureId creature, BoardCoordinate position) {
        getFactory().getSpell(creature).summon(creature + "_" + UUID.randomUUID(), getBoard().getTile(position), player, true);
        state.reduceMoney(player, creature.getCost());
    }

    public void spawnTrapAt(PlayerId player, TrapId trap, BoardCoordinate position) {
        getSpawner().spawnTrap(trap, player, getBoard().getTile(position));
        state.reduceMoney(player, trap.getCost());
    }

    // NOTE: Thread return is for the server and should probably be optimized
    public Optional<Thread> performAction(TurnActionMessage message) {
        Optional<Creature> mayCreature = getFactory().get(message.getFrom());
        if (mayCreature.isEmpty()) {
            HexStartLogger.log().log(Level.SEVERE, "ActionMessage could not be performed, as no performer was found in: {0}", message.toJson());
            return Optional.empty();
        }
        final Creature creature = mayCreature.get();

        switch (message.getAction()) {
            case MOVEMENT:
                Thread moveThread = new Thread(() -> processMovement(message, creature));
                moveThread.start();
                return Optional.of(moveThread);
            case SKILL:
                Thread skillThread = new Thread(() -> processSkill(message, creature));
                skillThread.start();
                return Optional.of(skillThread);
            case SKIP:
                creature.skip();
                break;
            default:
            case NONE:
        }
        return Optional.empty();
    }

    private void processSkill(TurnActionMessage message, Creature creature) {
        SkillId skillId = message.getSkillId();
        Optional<ICreatureSkill> maySkill = creature.getSkill(skillId);
        if(maySkill.isEmpty()) {
            HexStartLogger.log().log(Level.SEVERE, "ActionMessage could not be performed, as creature {1} does not possess skill requested by: {0}", new Object[]{ message.toJson(), creature });
            return;
        }
        ICreatureSkill skill = maySkill.get();
        switch (skill.getTarget()) {
            case CREATURE:
                Optional<Creature> mayTargetCreature = getFactory().get(message.getTarget());
                if (mayTargetCreature.isEmpty()) {
                    HexStartLogger.log().log(Level.SEVERE, "ActionMessage could not be performed, as skill target needed to be, but was no creature in: {0}", message.toJson());
                    return;
                }
                creature.useSkill(getBoard(), skill, mayTargetCreature.get());
                creature.getAttributes().useAp(skill.getCost());
                break;
            case TILE:
                Tile targetTile = getBoard().getTile(message.getTarget());
                creature.useSkill(getBoard(), skill, targetTile);
                creature.getAttributes().useAp(skill.getCost());
                break;
            default:
                HexStartLogger.log().log(Level.SEVERE, "ActionMessage could not be performed, as skill target-type was invalid ({1}): {0}", new Object[]{message.toJson(), skill.getTarget()});
        }
    }

    private void processMovement(TurnActionMessage message, final Creature creature) {
        List<BoardCoordinate> targets = message.getTargets();
        for (BoardCoordinate target : targets) {
            CreatureActionController.processMovementBlocking(context.getTraps(), creature, getBoard().getTile(target));
            creature.getAttributes().useMp();
        }
    }

    public void giftRestOfMoney(PlayerId player) {
        int moneyLeft = player.ifOne(state.getMoneyP1Left(), state.getMoneyP2Left());
        HexStartLogger.log().log(Level.INFO, "Player {0} gifts rest of his money ({1})", new Object[] {player, moneyLeft});
        state.reduceMoney(player, moneyLeft);
    }
}

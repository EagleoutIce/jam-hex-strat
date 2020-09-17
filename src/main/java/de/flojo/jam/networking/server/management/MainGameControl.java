package de.flojo.jam.networking.server.management;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import de.flojo.jam.game.board.Board;
import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.terrain.management.Terrain;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.board.traps.TrapSpawner;
import de.flojo.jam.game.creature.Creature;
import de.flojo.jam.game.creature.CreatureFactory;
import de.flojo.jam.game.creature.CreatureId;
import de.flojo.jam.game.creature.controller.CreatureActionController;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.messages.GameStartMessage;
import de.flojo.jam.networking.messages.ItIsYourTurnMessage;
import de.flojo.jam.networking.messages.NextRoundMessage;
import de.flojo.jam.networking.messages.TurnActionMessage;
import de.flojo.jam.networking.messages.YouCanBuildMessage;
import de.flojo.jam.networking.server.ClientServerConnection;
import de.flojo.jam.networking.server.PlayerController;
import de.flojo.jam.networking.server.ServerController;
import de.flojo.jam.util.IProvideContext;
import de.gurkenlabs.litiengine.Game;

public class MainGameControl {

    private PlayerController playerController;
    private ServerController controller;
    private IProvideContext context;
    private GameState state;

    public MainGameControl(ServerController controller, IProvideContext context) {
        this.controller = controller;
        this.playerController = controller.getPlayerController();
        this.context = context;
        this.state = new GameState();
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
        Game.log().log(Level.INFO, "Sending build request to {0}.", currentPlayer);
    }

    public void buildTerrainAt(PlayerId player, TerrainId terrain, BoardCoordinate position) {
        context.getArchitect().placeImprint(position, terrain.getImprint());
        state.reduceMoney(player, terrain.getCost());
    }

    public boolean nextBuildRequest() {
        if(state.nextBuild()) {
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
        if(isGameOver()){
            // GAME OVER
            Game.log().info("Game Over");
            return false;
        } else if(roundEnd()) {
            nextRound();
        }
        sendTurnRequest();
        return true;
    }

    private void sendTurnRequest() {
        ClientServerConnection currentPlayer = controller.getPlayerController()
                .getPlayer(state.getCurrentTurn());
        currentPlayer.send(new ItIsYourTurnMessage(null));
        Game.log().log(Level.INFO, "Sending game-turn request to {0}.", currentPlayer);
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

    public void summonCreatureAt(PlayerId player, CreatureId creature, BoardCoordinate position) {
        getFactory().getSpell(creature).summon(creature + "_" + UUID.randomUUID(), getBoard().getTile(position), player);
        state.reduceMoney(player, creature.getCost());
	}

	public void spawnTrapAt(PlayerId player, TrapId trap, BoardCoordinate position) {
        getSpawner().spawnTrap(trap, player, getBoard().getTile(position));
        state.reduceMoney(player, trap.getCost());
	}

	public void performAction(TurnActionMessage message) {
        Optional<Creature> mayCreature = getFactory().get(message.getFrom());
        if(mayCreature.isEmpty()) {
            Game.log().log(Level.SEVERE, "ActionMessage could not be performed, as no performer was found in: {0}", message);
            return;
        }
        final Creature creature = mayCreature.get();

        switch(message.getAction()) {
            case MOVEMENT:
                new Thread(() -> processMovement(message, creature)).start();
                break;
            case SKILL:
                Optional<Creature> mayTarget = getFactory().get(message.getTarget());
                if(mayTarget.isEmpty()) {
                    Game.log().log(Level.SEVERE, "ActionMessage could not be performed, as skill target was no creature in: {0}", message);
                    return;
                }
                creature.useSkill(getBoard(), message.getSkillId(), mayTarget.get());
                break;
            case SKIP:
                creature.skip();
                break;
            default:
            case NONE:
        }
	}

    private void processMovement(TurnActionMessage message, final Creature creature) {
        List<BoardCoordinate> targets = message.getTargets();
        for (BoardCoordinate target : targets) {
            CreatureActionController.processMovementBlocking(context.getTraps(), creature, getBoard().getTile(target));
        }
    }

}

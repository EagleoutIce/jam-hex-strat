package de.flojo.jam.networking.server.management;

import java.util.UUID;
import java.util.logging.Level;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.terrain.TerrainMap;
import de.flojo.jam.game.board.terrain.management.Terrain;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.game.board.traps.TrapId;
import de.flojo.jam.game.creature.CreatureId;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.messages.BuildChoiceMessage;
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
        this.context = context;
        this.state = new GameState();
    }

    public TerrainMap getTerrainMap() {
        return context.getBoard().getTerrainMap();
    }

    public Terrain getTerrain() {
        return getTerrainMap().getTerrain();
    }

    public boolean dealWithBuildChoice(BuildChoiceMessage choice) {
        // we do not check if the build is valid as i do not want to duplicate checking
        // really
        // we place the stuff and if it breaks we will end the session
        return true;

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
        nextGameAction();
    }

    private void nextGameAction() {
        state.nextPlayer();
        if(isGameOver()){
            // GAME OVER
            Game.log().info("Game Over");
        } else if(roundEnd()) {
            state.nextRound();
            context.getFactory().resetAll();
        }
    }

	private boolean roundEnd() {
        return context.getFactory().noneCanDoSomething();
    }

    private boolean isGameOver() {
        return !context.getFactory().playerOneOwns() || !context.getFactory().playerTwoOwns();
    }

    public void summonCreatureAt(PlayerId player, CreatureId creature, BoardCoordinate position) {
        context.getFactory().getSpell(creature).summon(creature + "_" + UUID.randomUUID(), context.getBoard().getTile(position), player);
        state.reduceMoney(player, creature.getCost());
	}

	public void spawnTrapAt(PlayerId player, TrapId trap, BoardCoordinate position) {
        context.getSpawner().spawnTrap(trap, player, context.getBoard().getTile(position));
        state.reduceMoney(player, trap.getCost());
	}

}

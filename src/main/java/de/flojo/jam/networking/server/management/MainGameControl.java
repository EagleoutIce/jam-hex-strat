package de.flojo.jam.networking.server.management;

import de.flojo.jam.game.board.BoardCoordinate;
import de.flojo.jam.game.board.terrain.management.Terrain;
import de.flojo.jam.game.board.terrain.management.TerrainId;
import de.flojo.jam.networking.messages.BuildChoiceMessage;
import de.flojo.jam.networking.messages.YouCanBuildMessage;
import de.flojo.jam.networking.server.ClientServerConnection;
import de.flojo.jam.networking.server.PlayerController;
import de.flojo.jam.networking.server.ServerController;
import de.flojo.jam.util.IProvideContext;

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

    public Terrain getTerrain() {
        return context.getBoard().getTerrainMap().getTerrain();
    }

    public boolean dealWithBuildChoice(BuildChoiceMessage choice) {
        // we do not check if the build is valid as i do not want to duplicate checking
        // really
        // we place the stuff and if it breaks we will end the session
        return true;

    }

    public void startBuildPhase() {
        // send request message to first player
        ClientServerConnection clientServerConnection = controller.getPlayerController()
                .getPlayer(state.getCurrentTurn());
        clientServerConnection.send(new YouCanBuildMessage(null, state.currentRound));
    }

	public void placeTerrainAt(TerrainId terrain, BoardCoordinate position) {
        context.getArchitect().placeImprint(position, terrain.getImprint());
	}

}

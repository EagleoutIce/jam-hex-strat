package de.flojo.jam.networking.client;

import java.util.UUID;

import de.flojo.jam.game.board.terrain.management.Terrain;
import de.flojo.jam.game.player.PlayerId;
import de.flojo.jam.networking.messages.BuildPhaseStartMessage;
import de.flojo.jam.networking.messages.HelloReplyMessage;

public class ClientContext {

    private UUID myId;
    private Terrain terrain;

    private UUID p1Id;
    private UUID p2Id;

    private String p1Name;
    private String p2Name;

    private PlayerId myPlayerId;

    public ClientContext() {
        super();
    }

    public UUID getMyId() {
        return myId;
    }

    public void setMyId(UUID myId) {
        this.myId = myId;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public void handleHelloReply(HelloReplyMessage message){
        this.myId = message.getClientId();
        this.terrain = message.getTerrain();
    }

    public void handleGameStart(BuildPhaseStartMessage message){
        this.p1Id = message.getP1Id();
        this.p2Id = message.getP2Id();
        this.p1Name = message.getP1Name();
        this.p2Name = message.getP2Name();
        this.myPlayerId = myId.equals(p1Id) ? PlayerId.ONE : PlayerId.TWO;
    }

    public UUID getP1Id() {
        return p1Id;
    }

    public UUID getP2Id() {
        return p2Id;
    }

    public PlayerId getMyPlayerId() {
        return myPlayerId;
    }

    public String getP1Name() {
        return p1Name;
    }

    public String getP2Name() {
        return p2Name;
    }
}


package de.flojo.jam.networking.client;

import java.util.UUID;

import de.flojo.jam.game.board.terrain.management.Terrain;
import de.flojo.jam.networking.messages.HelloReplyMessage;

public class ClientContext {

    UUID myId;
    Terrain terrain;

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

}

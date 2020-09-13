package de.flojo.jam.networking.client;

import de.flojo.jam.networking.messages.HelloMessage;

public class ClientSender {
    
    private IClientController controller;


    public ClientSender(final IClientController controller) {
        this.controller = controller;
    }



    public void sendHello(String name){
        controller.send(new HelloMessage(name));
    }


}

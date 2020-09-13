package de.flojo.jam.util;

import java.util.Set;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.input.IKeyboard.KeyPressedListener;
import de.gurkenlabs.litiengine.input.Input;

public class InputController {
 
    
    private static final InputController instance = new InputController();
    

    public InputController() {
        //
    }


    public static InputController get() {
        return instance;
    }


    public void onKeyPressed(int keyCode, KeyPressedListener eventHandler, Set<String> screens) {
        Input.keyboard().onKeyPressed(keyCode, ke -> {
            if(!Game.window().isFocusOwner() || !screens.contains(Game.screens().current().getName()))
                return;
            eventHandler.keyPressed(ke);
        });
    }

    public void onKeyPressed(int keyCode, KeyPressedListener eventHandler, Set<String> screens, InputGroup<Integer> group) {
        Input.keyboard().onKeyPressed(keyCode, ke -> {
            if(!Game.window().isFocusOwner() || !screens.contains(Game.screens().current().getName()))
                return;
            if(!group.tryLock(keyCode))
                return;
            eventHandler.keyPressed(ke);
        });
    }

}

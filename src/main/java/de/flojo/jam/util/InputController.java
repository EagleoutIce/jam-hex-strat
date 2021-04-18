package de.flojo.jam.util;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.input.IKeyboard.KeyPressedListener;
import de.gurkenlabs.litiengine.input.IKeyboard.KeyTypedListener;
import de.gurkenlabs.litiengine.input.IMouse.MouseClickedListener;
import de.gurkenlabs.litiengine.input.IMouse.MouseMovedListener;
import de.gurkenlabs.litiengine.input.Input;

import java.util.Set;

public class InputController {


    private static final InputController instance = new InputController();


    public InputController() {
        //
    }


    public static InputController get() {
        return instance;
    }


    public void onKeyPressed(int keyCode, KeyPressedListener eventHandler, String screen) {
        onKeyPressed(keyCode, eventHandler, Set.of(screen));
    }

    public void onKeyPressed(int keyCode, KeyPressedListener eventHandler, Set<String> screens) {
        Input.keyboard().onKeyPressed(keyCode, ke -> {
            if (!Game.window().isFocusOwner() || !screens.contains(Game.screens().current().getName()))
                return;
            eventHandler.keyPressed(ke);
        });
    }

    public void onKeyPressed(int keyCode, KeyPressedListener eventHandler, Set<String> screens, InputGroup<Integer> group) {
        Input.keyboard().onKeyPressed(keyCode, ke -> {
            if (!Game.window().isFocusOwner() || !screens.contains(Game.screens().current().getName()))
                return;
            if (!group.tryLock(keyCode))
                return;
            eventHandler.keyPressed(ke);
        });
    }

    public void onClicked(MouseClickedListener eventHandler, String screen) {
        onClicked(eventHandler, Set.of(screen));
    }

    public void onClicked(MouseClickedListener eventHandler, Set<String> screens) {
        Input.mouse().onClicked(mc -> {
            if (!Game.window().isFocusOwner() || !screens.contains(Game.screens().current().getName()))
                return;
            eventHandler.mouseClicked(mc);
        });
    }

    public void onMoved(MouseMovedListener eventHandler, String screen) {
        onMoved(eventHandler, Set.of(screen));
    }

    public void onMoved(MouseMovedListener eventHandler, Set<String> screens) {
        Input.mouse().onMoved(mm -> {
            if (!Game.window().isFocusOwner() || !screens.contains(Game.screens().current().getName()))
                return;
            eventHandler.mouseMoved(mm);
        });
    }


    public void onKeyTyped(int keyCode, KeyTypedListener eventHandler, String screen) {
        onKeyTyped(keyCode, eventHandler, Set.of(screen));
    }

    public void onKeyTyped(int keyCode, KeyTypedListener eventHandler, Set<String> screens) {
        Input.keyboard().onKeyTyped(keyCode, ke -> {
            if (!Game.window().isFocusOwner() || !screens.contains(Game.screens().current().getName()))
                return;
            eventHandler.keyTyped(ke);
        });
    }

    public void onKeyTyped(int keyCode, KeyTypedListener eventHandler, Set<String> screens, InputGroup<Integer> group) {
        Input.keyboard().onKeyTyped(keyCode, ke -> {
            if (!Game.window().isFocusOwner() || !screens.contains(Game.screens().current().getName()))
                return;
            if (!group.tryLock(keyCode))
                return;
            eventHandler.keyTyped(ke);
        });
    }

}

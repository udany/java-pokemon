package engine.input;

import engine.util.Event;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class Keyboard {
    public Event<Integer> onKeyDown = new Event<>();
    public Event<Integer> onKeyUp = new Event<>();
    private HashMap<Integer, Boolean> keysPressed = new HashMap<>();

    private Keyboard(){
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        if (!isPressed(e.getKeyCode())){
                            keysPressed.put(e.getKeyCode(), true);
                            onKeyDown.emit(e.getKeyCode());
                        }
                    } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                        keysPressed.put(e.getKeyCode(), false);
                        onKeyUp.emit(e.getKeyCode());
                    }
                    return false;
                });
    }

    private static Keyboard instance;
    public static Keyboard getInstance(){
        if (instance == null) instance = new Keyboard();

        return instance;
    }

    public boolean isPressed(int keyCode){
        Boolean val = keysPressed.get(keyCode);
        if(val != null){
            return val;
        }
        return false;
    }
}

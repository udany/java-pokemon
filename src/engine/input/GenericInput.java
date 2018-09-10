package engine.input;

import engine.util.Event;

import java.awt.event.KeyEvent;
import java.util.*;

public class GenericInput {
    public class Button {
        public static final int A = 1;
        public static final int B = 2;
        public static final int X = 3;
        public static final int Y = 4;

        public static final int L = 5;
        public static final int R = 6;

        public static final int Start = 7;
        public static final int Select = 8;

        public static final int Up = 9;
        public static final int Down = 10;
        public static final int Left = 11;
        public static final int Right = 12;
    }
    private HashMap<Integer, Boolean> buttonsPressed = new HashMap<>();

    public final UUID id = UUID.randomUUID();

    public final Event<Integer> onButtonDown = new Event<>();
    public final Event<Integer> onButtonUp = new Event<>();

    public GenericInput() {
        Keyboard kb = Keyboard.getInstance();

        kb.onKeyDown.addListener(code -> {
            if (keyboardAlias.containsKey(code)) {
                for (Integer btnCode : keyboardAlias.get(code)) {
                    onButtonDown.emit(btnCode);
                }
            }
        });
        kb.onKeyUp.addListener(code -> {
            if (keyboardAlias.containsKey(code)) {
                for (Integer btnCode : keyboardAlias.get(code)) {
                    onButtonUp.emit(btnCode);
                }
            }
        });


        onButtonDown.addListener(btn -> buttonsPressed.put(btn, true));
        onButtonUp.addListener(btn -> buttonsPressed.put(btn, false));
    }

    public GenericInput applyDefaultBinding() {
        // KB
        aliasKb(KeyEvent.VK_Z, Button.A);
        aliasKb(KeyEvent.VK_X, Button.B);
        aliasKb(KeyEvent.VK_C, Button.X);
        aliasKb(KeyEvent.VK_V, Button.Y);

        aliasKb(KeyEvent.VK_A, Button.L);
        aliasKb(KeyEvent.VK_S, Button.R);

        aliasKb(KeyEvent.VK_ENTER, Button.Start);
        aliasKb(KeyEvent.VK_BACK_SPACE, Button.Select);

        aliasKb(KeyEvent.VK_UP, Button.Up);
        aliasKb(KeyEvent.VK_DOWN, Button.Down);
        aliasKb(KeyEvent.VK_LEFT, Button.Left);
        aliasKb(KeyEvent.VK_RIGHT, Button.Right);

        return this;
    }

    public GenericInput aliasKb(Integer key, Integer alias) {
        return aliasKb(key, new ArrayList<>(Arrays.asList(alias)));
    }

    public GenericInput aliasKb(Integer key, ArrayList<Integer> alias) {
        if (!keyboardAlias.containsKey(key)) {
            keyboardAlias.put(key, alias);
        } else {
            keyboardAlias.get(key).addAll(alias);
        }
        return this;
    }

    public boolean isPressed(int btn){
        Boolean val = buttonsPressed.get(btn);
        if(val != null){
            return val;
        }
        return false;
    }

    HashMap<Integer, ArrayList<Integer>> keyboardAlias = new HashMap<>();
}

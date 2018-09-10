package Pokemon;
import Pokemon.game.Player;
import engine.input.GenericInput;
import engine.main.Stage;
import engine.window.Game;

import java.awt.event.KeyEvent;

public class PokemonGame extends Game {
    public PokemonGame() {
        super(1280, 720);
        setTitle("Pokemon");
        frameRate = 60;

        Player player = new Player();
        player.setPosition(480, 480);

        addObject(player);

        start();
    }

    static GenericInput controller;
    public GenericInput getInput() {
        if (controller == null){
            controller = new GenericInput();
            controller.applyDefaultBinding();
        }

        return controller;
    }
}

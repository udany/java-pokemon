package Pokemon;

import Pokemon.game.Flower;
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

        Flower flower;

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {

                flower = new Flower();
                flower.setPosition(320 + x*16, 320 + y*16);
                addObject(flower);

            }
        }

        Player player = new Player();
        player.setPosition(480, 480);
        addObject(player);

        start();
    }

    static GenericInput controller;

    public GenericInput getInput() {
        if (controller == null) {
            controller = new GenericInput();
            controller.applyDefaultBinding();
        }

        return controller;
    }
}

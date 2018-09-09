package Pokemon;
import engine.input.GenericInput;
import engine.main.Stage;
import engine.window.Game;

public class PokemonGame extends Game {
    public PokemonGame() {
        this(null);
    }

    public PokemonGame(Stage startingStage) {
        super(1280, 720);
        setTitle("Galaxy Fighter");
        debug = true;
        frameRate = 60;

        start();
    }

    static GenericInput controller;

    public GenericInput getInput() {
        if (controller == null) controller = new GenericInput();
        controller.applyDefaultBinding();

        return controller;
    }
}

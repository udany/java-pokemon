package run;

import Pokemon.PokemonGame;
import engine.window.Game;

public class Main {
    public static void main(String[] args) {
        Game g = new PokemonGame();
        g.open();
    }
}

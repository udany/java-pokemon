package engine.window.Transition;

import engine.graphics.Color;
import engine.window.Game;

import java.awt.*;

public class FadeIn extends Transition {
    public FadeIn(Color c, double duration, Game game) {
        super(duration, game);
        color = c;
    }

    double alpha;
    Color color;

    @Override
    public void start() {
        super.start();
        alpha = 1;
    }

    @Override
    public void step(double ratio) {
        alpha = 1 - ratio;
    }

    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(color.scaleAlpha(alpha));
        graphics.fillRect(0, 0, game.size.width, game.size.height);
    }
}
package engine.window.Transition;

import engine.util.Event;
import engine.window.Game;
import engine.window.TweenHelper;

import java.awt.*;


public abstract class Transition {
    protected Game game;
    protected TweenHelper tween;
    protected Transition next;
    public Event onEnd = new Event();

    public Transition(double duration, Game g) {
        game = g;
        tween = new TweenHelper(duration, this::step, game.onUpdate);
        tween.onEnd.addListener(x -> onEnd.emit());
    }

    public boolean isFinished() { return tween.isFinished(); }

    public Transition chain(Transition next) {
        this.next = next;

        return this;
    }

    public boolean hasNext() {
        return next != null;
    }
    public Transition next() {
        if (next != null) next.start();

        return next;
    }

    public void start() {
        tween.start();
    }

    public abstract void step(double ratio);
    public abstract void draw(Graphics2D graphics);
}

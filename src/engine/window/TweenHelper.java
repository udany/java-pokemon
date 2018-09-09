package engine.window;

import engine.util.Event;

import java.util.UUID;

public class TweenHelper {
    UUID id = UUID.randomUUID();
    double length;
    double totalElapsed = 0;
    TweenStep step;
    Event<Double> onUpdate;
    public Event onEnd = new Event();

    public TweenHelper(double l, TweenStep s, Event<Double> u) {
        length = l;
        totalElapsed = length;
        step = s;
        onUpdate = u;
    }

    public void start() {
        if (!this.isFinished()) return;
        totalElapsed = 0;

        onUpdate.addListener(e -> {
            update(e);

            if (totalElapsed == length) {
                onUpdate.removeListener(id.toString());
                onEnd.emit();
            }
        }, id.toString());
    }

    public void reset() {
        onUpdate.removeListener(id.toString());
        totalElapsed = length;
    }

    public boolean isFinished() { return totalElapsed == length; }

    public void update(double elapsed) {
        totalElapsed += elapsed;
        if (totalElapsed > length) totalElapsed = length;

        step.step(totalElapsed/length);
    }

    public interface TweenStep<T> {
        void step(double progress);
    }
}

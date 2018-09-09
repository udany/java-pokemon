package engine.main;

import engine.base.Size;
import engine.graphics.Color;
import engine.input.GenericInput;
import engine.input.Keyboard;
import engine.util.Event;
import engine.window.Game;
import engine.window.Transition.FadeIn;
import engine.window.Transition.FadeOut;
import engine.window.Transition.Transition;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Stage {
    public Stage() {
        objectList = new ArrayList<>();
    }

    protected UUID id = UUID.randomUUID();
    protected Game game;
    public Game getGame() { return game; }
    public long getGameTime() { return game.getGameTime(); }

    public Size size;
    public Event<Graphics2D> onPaint = new Event<>();
    public Event<Long> onUpdate = new Event<>();
    public Event<Game> onAdd = new Event<Game>().addListener(g -> {
        game = g;
        this.size = game.size;

        tree = new QuadTree(size.width, size.height);

        bindToController(game.getInput());
        unpause();
    });

    public Event<Game> onRemove = new Event<Game>().addListener(g -> {
        pause();

        if (game == null) return;

        unbindToController(game.getInput());

        game = null;
    });

    public Event<Integer> onButtonDown = new Event<>();
    public Event<Integer> onButtonUp = new Event<>();

    protected void bindToController(GenericInput input) {
        input.onButtonDown.addListener(key -> {
            // if (transitioning) return;
            onButtonDown.emit(key);

            if (key == GenericInput.Button.Select) {
                if (paused) { unpause(); } else { pause(); }
            }
        }, id.toString());

        input.onButtonUp.addListener(key -> {
            // if (transitioning) return;
            onButtonUp.emit(key);
        }, id.toString());
    }

    protected void unbindToController(GenericInput input) {
        input.onButtonDown.removeListener(id.toString());
        input.onButtonUp.removeListener(id.toString());
    }

    private QuadTree tree;
    private List<GameObject> objectList;

    protected boolean paused = false;
    public Stage pause() {
        paused = true;
        return this;
    }
    public Stage unpause() {
        paused = false;
        return this;
    }

    public void draw(Graphics2D graphics){
        AffineTransform baseTransform = graphics.getTransform();
        AffineTransform currentTransform;

        for(GameObject o : objectList){
            o.draw(graphics);

            currentTransform = graphics.getTransform();
            if (!currentTransform.equals(baseTransform)) {
                graphics.setTransform(baseTransform);
            }
        }

        onPaint.emit(graphics);
    }

    protected List<GameObject> objectsToAdd = new ArrayList<>();
    public void addObject(GameObject obj) {
        objectsToAdd.add(obj);
    }

    public List<GameObject> getObjectList() {
        return objectList;
    }
    public List<GameObject> getObjectList(Predicate<GameObject> test) {
        return objectList.stream().filter(test).collect(Collectors.toList());
    }

    public void update(long elapsedMs){
        if (paused) return;

        /// Remove Objects
        List<GameObject> objectsToRemove = getObjectList(GameObject::isDestroyed);
        objectList.removeAll(objectsToRemove);
        objectsToRemove.forEach(obj -> obj.onRemove.emit(this));

        // Remove Solid Objects from tree
        objectsToRemove = objectsToRemove.stream().filter(GameObject::isSolid).collect(Collectors.toList());
        tree.removeAll(objectsToRemove);

        // Add Objects
        objectList.addAll(objectsToAdd);
        tree.insertAll(objectsToAdd.stream().filter(GameObject::isSolid).collect(Collectors.toList()));
        objectsToAdd.forEach(obj -> obj.onAdd.emit(this));
        objectsToAdd.clear();

        // Update Objects
        List<GameObject> list = new ArrayList<>();
        list.addAll(objectList);
        for(GameObject o : list){
            o.update(((double)elapsedMs) / 1000);

            if (o.hasMoved() && o.isSolid()) {
                tree.update(o);
            }
        }

        tree.prune();
        onUpdate.emit(elapsedMs);
        collisionChecking();
    }

    protected void collisionChecking(){
        List<GameObject> objectsToCheck = objectList.stream().filter(GameObject::checkForCollisions).collect(Collectors.toList());
        List<GameObject> potentialCollisions;

        for (GameObject obj : objectsToCheck){
            potentialCollisions = tree.retrieve(obj);

            for (GameObject solidObj : potentialCollisions){
                Shape objShape = obj.getCollisionArea();
                Shape solidShape = solidObj.getCollisionArea();

                if (objShape.intersects(solidShape.getBounds())){
                    obj.onCollision.emit(solidObj);
                }
            }
        }
    }

    protected boolean transitioning = false;
    public void transitionTo(Stage to) {
        pause();
        FadeOut fadeOut = new FadeOut(new engine.graphics.Color(0,0,0), .5, game);
        FadeIn fadeIn = new FadeIn(new Color(0,0,0), .5, game);
        fadeOut.chain(fadeIn);

        transitionTo(to, fadeOut);
    }
    public void transitionTo(Stage to, Transition transition) {
        transition.onEnd.addListener(x -> {
            swapFor(to);
        });

        transitioning = true;
        game.transition(transition);
    }

    public void swapFor(Stage to) {
        if (game != null) {
            game.removeStage(this);
            game.addStage(to);

            transitioning = false;
        }
    }
}

package engine.main;

import engine.base.*;
import engine.graphics.Sprite;
import engine.util.Event;
import engine.util.EventData;

import java.awt.*;
import java.util.UUID;

public abstract class GameObject implements IObject {
    protected Size size = new Size();
    protected Vector position = new Vector();
    protected Sprite currentSprite;
    protected boolean debug = false;
    protected UUID id = UUID.randomUUID();

    public String getId() {
        return id.toString();
    }

    public int getWidth(){
        return size.width;
    }

    public int getHeight(){
        return size.height;
    }

    public double getX(){
        return position.x;
    }

    public double getY(){
        return position.y;
    }

    public GameObject setX(double x){
        position.x = x;
        return this;
    }
    public GameObject setY(double y){
        position.y = y;
        return this;
    }

    public GameObject setPosition(double x, double y){
        setX(x);
        setY(y);
        return this;
    }
    public GameObject setPosition(Vector p){
        position.set(p.x, p.y);
        return this;
    }

    public GameObject alignToGrid(double x, double y){
        setX(
                Math.round((getX()/x))*x
        );
        setY(
                Math.round((getY()/y))*y
        );

        return this;
    }

    private Vector lastPosition;
    public void update(double secondsElapsed) {
        lastPosition = position.clone();
    }
    public boolean hasMoved(){
        return lastPosition == null || !position.equals(lastPosition);
    }

    public void draw (Graphics2D graphics){
        if (currentSprite != null){
            currentSprite.draw(graphics, position);
        }

        if (debug) {
            Vector p = position.clone().add(-10, getHeight()+10);

            graphics.setFont( new Font( "Courier New", Font.PLAIN, 9 ) );
            graphics.setColor(new Color(0, 255, 3,100 ));
            graphics.drawString(String.format("%s", id.toString().split("-")[4]), (int) p.x, (int) p.y);

            graphics.setColor(new Color(24, 30, 255, 157));
            graphics.draw(getCollisionArea());
        }
    }

    public Shape getCollisionArea(){
        return new Rectangle((int)position.x, (int)position.y, size.width, size.height);
    }

    public Vector getCenter(){
        return new Vector(position.x + (size.width/2), position.y + (size.height/2));
    }

    public Event<GameObject> onCollision = new Event<>();

    protected boolean checkForCollisions = false;
    /**
     * Means it should check for collisions with other objects
     * @return
     */
    public boolean checkForCollisions(){
        return checkForCollisions;
    }

    protected boolean isSolid = false;
    /**
     * Means another object can collide with this
     * @return boolean
     */
    public boolean isSolid(){
        return isSolid;
    }

    public Event<EventData> onDestroy = new Event<>();

    public boolean isDestroyed() {
        return destroyed;
    }

    private boolean destroyed = false;

    public void destroy(){
        destroyed = true;
        onDestroy.emit();
    }

    protected Stage currentStage;

    public Event<Stage> onAdd = new Event<Stage>().addListener(s -> {
        currentStage = s;
    });
    public Event<Stage> onRemove = new Event<>();
}

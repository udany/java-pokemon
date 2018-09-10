package Pokemon.game;

import engine.graphics.Sprite;
import engine.main.GameObject;

import java.awt.*;

public class Flower extends GameObject {

    public Flower() {
        currentSprite = new Sprite(16, 16, "/images/flower.png")
                .setFramesPerFrame(8);

        isSolid = true;
        size.set(16, 16);
    }

    public void explode() {

        currentSprite = new Sprite(16, 16, "/images/Explosion_small.png")
                .setFramesPerFrame(8);

        currentSprite.onAnimationEnd.addListener(e -> this.destroy() );
    }


    @Override
    public Shape getCollisionArea() {
        int inset = 3;

        return new Rectangle((int) position.x + inset, (int) position.y + inset, size.width - inset * 2, size.height - inset * 2);
    }
}

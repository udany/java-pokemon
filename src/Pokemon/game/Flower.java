package Pokemon.game;

import engine.graphics.Sprite;
import engine.main.GameObject;

public class Flower extends GameObject {

    public Flower(){
        currentSprite = new Sprite(16, 16,"/images/flower.png")
                .setFramesPerFrame(8);


        size.set(16,16);
    }

}

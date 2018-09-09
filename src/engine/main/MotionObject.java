package engine.main;

import engine.base.Vector;

import java.awt.*;

public abstract class MotionObject extends GameObject {
    public Vector speed = new Vector(0,0);
    public Vector acceleration = new Vector(0,0);

    @Override
    public void update(double secondsElapsed) {
        super.update(secondsElapsed);
        speed.add(acceleration.x*secondsElapsed, acceleration.y * secondsElapsed);
        position.add(speed.x*secondsElapsed, speed.y * secondsElapsed);
    }

    @Override
    public void draw(Graphics2D graphics) {
        super.draw(graphics);

        if (debug) {
            Vector p = position.clone().subtract(10, 12);

            graphics.setFont( new Font( "Courier New", Font.PLAIN, 10 ) );
            graphics.setColor(new Color(255, 15, 100,100 ));

            graphics.drawString(String.format("Speed: [%s, %s]", (int)speed.x, (int)speed.y), (int) p.x, (int) p.y-10 );
            graphics.drawString(String.format("Accel: [%s, %s]", (int)acceleration.x, (int)acceleration.y), (int) p.x, (int) p.y );
        }
    }
}

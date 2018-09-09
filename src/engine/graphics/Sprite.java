package engine.graphics;

import engine.base.Vector;
import engine.base.Size;
import engine.resources.ResourceLoader;
import engine.util.Event;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Sprite {
    String fileName;
    protected BufferedImage image;
    protected Size size = new Size();
    protected int frameCount = 0;
    protected int state = 0;
    protected int stateCount = 0;
    protected int currentVirtualFrame = 0;
    protected int framesPerFrame = 1;

    public Vector origin = new Vector(0, 0);
    public Sprite setOrigin(double x, double y) {
        origin.set(x, y);
        return this;
    }

    public Event onAnimationEnd = new Event();

    public Sprite(int w, int h, String file){
        size.width = w;
        size.height = h;

        setImage(file);
    }

    // Basic image methods
    public void setImage(String file) {
        fileName = file;
        image = ResourceLoader.loadImage(file);

        if (image == null) {
            System.out.println("Couldn't load sprite: "+file);
        }
    }

    public void setImage(BufferedImage img) {
        image = img;
    }

    public int getWidth(){
        return size.width;
    }

    public int getHeight(){
        return size.height;
    }

    public Vector getCenter(){
        return new Vector(size.width/2, size.height/2);
    }

    // Frame count and iteration logic
    public int getStateCount(){
        if (stateCount != 0){
            return stateCount;
        }
        if (image != null){
            stateCount = image.getHeight() / getHeight();
            return stateCount;
        }
        return 0;
    }

    public int getFrameCount(){
        if (frameCount != 0){
            return frameCount;
        }
        if (image != null){
            frameCount = image.getWidth() / getWidth();
            return frameCount;
        }
        return 0;
    }

    public int getVirtualFrameCount(){
        return getFrameCount()*framesPerFrame;
    }

    public void nextFrame(){
        if (currentVirtualFrame == getVirtualFrameCount()-1){
            currentVirtualFrame = 0;
        }else{
            currentVirtualFrame++;
        }

        if (currentVirtualFrame == getVirtualFrameCount()-1){
            onAnimationEnd.emit();
        }
    }

    public void setFrame(int f){
        currentVirtualFrame = f * framesPerFrame;
    }

    public int getCurrentFrame(){
        return (int)Math.floor(currentVirtualFrame/framesPerFrame);
    }

    public Sprite setFramesPerFrame(int framesPerFrame) {
        this.framesPerFrame = framesPerFrame;
        return this;
    }

    public Sprite setState(int state) {
        this.state = state % this.getStateCount();
        return this;
    }

    public int getState() {
        return this.state;
    }


    // Animation flag
    private boolean _animate = true;
    public boolean animate() {
        return _animate;
    }
    public Sprite animate(boolean v) {
        _animate = v;
        return this;
    }


    public Sprite draw (Graphics2D graphics, Vector position){
        if (animate()) nextFrame();

        Vector d1 = position.clone().subtract(origin);
        Vector d2 = position.clone().subtract(origin).add(size);

        int cFrame = getCurrentFrame();

        Vector s1 = new Vector(size.width * cFrame, size.height*state);
        Vector s2 = s1.clone().add(size);

        rotateGraphics(graphics, position);
        graphics.drawImage(image,
                (int)d1.x, (int)d1.y, (int)d2.x, (int)d2.y,
                (int)s1.x, (int)s1.y, (int)s2.x, (int)s2.y, null);

        return this;
    }

    protected double rotation = 0;
    public Vector rotationCenter = null;
    private void rotateGraphics(Graphics2D graphics, Vector position) {
        if (rotation == 0) return;

        Vector center = rotationCenter != null ? rotationCenter : getCenter();

        center.subtract(origin).add(position);

        AffineTransform at = AffineTransform.getRotateInstance(
                rotation, center.x, center.y);

        graphics.transform(at);
    }

    public Sprite rotate(double degrees) {
        rotation = (degrees / 180) * Math.PI;

        return this;
    }




    /// PALETTES
    private Palette basePalette;
    public void setBasePalette(Palette p) {
        basePalette = p;
    }

    public void applyPalette(Palette p) {
        if (basePalette != null) {
            p.apply(this, basePalette);
            basePalette = p;
        }
    }
}

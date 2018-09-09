package engine.graphics;

import engine.base.Size;
import engine.base.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Tileset {

    protected BufferedImage image;
    protected Size size = new Size();
    protected int rows = 0;
    protected int cols = 0;

    public Tileset(int w, int h, String file){
        size.width = w;
        size.height = h;
        setImage(file);
    }

    public void setImage(BufferedImage img) {
        image = img;

        cols = image.getWidth() / size.width;
        rows = image.getHeight() / size.height;
    }

    public void setImage(String file) {
        URL url = getClass().getResource(file);

        try{
            setImage(ImageIO.read(url));
        }catch (Exception e){
            System.out.println("Failed loading image "+url);
        }
    }

    public int getRow(int index){
        return (int)Math.floor(index/cols);
    }
    public int getCol(int index){
        return index % cols;
    }

    public void draw (Graphics2D graphics, Vector p, int index){

        Vector d1 = p.clone();
        Vector d2 = p.clone().add(size);

        int col = getCol(index);
        int row = getRow(index);

        Vector s1 = new Vector(size.width * col, size.height * row);
        Vector s2 = s1.clone().add(size);

        graphics.drawImage(image,
                (int)d1.x, (int)d1.y, (int)d2.x, (int)d2.y,
                (int)s1.x, (int)s1.y, (int)s2.x, (int)s2.y, null);
    }
}

package engine.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageTools {
    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static BufferedImage flipHorizontal(BufferedImage image)
    {
        return flip(image, true, false);
    }

    public static BufferedImage flipVertical(BufferedImage image)
    {
        return flip(image, false, true);
    }

    public static BufferedImage flip(BufferedImage image, boolean horizontal, boolean vertical)
    {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(horizontal ? -1 : 1, vertical ? -1 : 1));
        at.concatenate(AffineTransform.getTranslateInstance(horizontal ? -image.getWidth() : 0, vertical ? -image.getHeight() : 0));
        return createTransformed(image, at);
    }

    public static BufferedImage rotate(BufferedImage image)
    {
        AffineTransform at = AffineTransform.getRotateInstance(
                Math.PI, image.getWidth()/2, image.getHeight()/2.0);
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at)
    {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}

package engine.graphics;

import engine.resources.ResourceLoader;
import engine.util.ImageTools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Palette {
    public List<Color> colors;

    public Palette(String file) {
        BufferedImage image = ResourceLoader.loadImage(file);

        colors = getColors(image);
    }

    public void apply(Sprite sprite, Palette base) {
        int w = sprite.image.getWidth();
        int h = sprite.image.getHeight();

        int[] rgb = sprite.image.getRGB(0, 0, w, h, null, 0, w);

        for (int i = 0; i < base.colors.size(); i++) {
            rgb = swapColor(rgb, base.colors.get(i), colors.get(i));
        }

        sprite.image = ImageTools.copyImage(sprite.image);
        sprite.image.setRGB(0, 0, w, h, rgb, 0, w);
    }

    private int RGB_MASK = 0x00ffffff;
    private int[] swapColor(int[] rgb, Color oldColor, Color newColor) {
        int oldRed = oldColor.getRed();
        int oldGreen = oldColor.getGreen();
        int oldBlue = oldColor.getBlue();
        int newRed = newColor.getRed();
        int newGreen = newColor.getGreen();
        int newBlue = newColor.getBlue();

        int oldRGB = oldRed << 16 | oldGreen << 8 | oldBlue;
        int toggleRGB = oldRGB ^ (newRed << 16 | newGreen << 8 | newBlue);

        for (int i = 0; i < rgb.length; i++) {
            if ((rgb[i] & RGB_MASK) == oldRGB) {
                rgb[i] ^= toggleRGB;
            }
        }

        return rgb;
    }

    public static String GetPalletFileName(String fn) {
        String[] strings = fn.split("/");
        fn = strings[strings.length-1];

        return fn.replace(".png", ".palette.0.png");
    }

    public static void GenerateFile(Sprite sprite) {
        List<Color> colors = getUniqueColors(sprite.image);

        BufferedImage basePallet = new BufferedImage(colors.size(), 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = basePallet.createGraphics();

        for (Color color : colors) {
            int idx = colors.indexOf(color);
            bGr.setColor(color);
            bGr.drawRect(idx, 0, 1, 1);
        }

        File outputFile = new File(GetPalletFileName(sprite.fileName));

        try {
            ImageIO.write(basePallet, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Color> getUniqueColors(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        int[] rgb = image.getRGB(0, 0, w, h, null, 0, w);

        ArrayList<String> list = new ArrayList<>();
        List<Color> colors = new ArrayList<>();

        for (int pixel : rgb) {
            Color color = new Color(pixel);

            String str = color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();

            if (!list.contains(str)) {
                list.add(str);
                colors.add(color);
            }
        }

        return colors;
    }

    public static List<Color> getColors(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        int[] rgb = image.getRGB(0, 0, w, h, null, 0, w);

        List<Color> colors = new ArrayList<>();

        for (int pixel : rgb) {
            Color color = new Color(pixel);
                colors.add(color);
        }

        return colors;
    }
}

package engine.graphics;

import java.awt.color.ColorSpace;

public class Color extends java.awt.Color {
    public Color(int r, int g, int b) {
        super(r, g, b);
    }

    public Color(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public Color(int rgb) {
        super(rgb);
    }

    public Color(int rgba, boolean hasalpha) {
        super(rgba, hasalpha);
    }

    public Color(float r, float g, float b) {
        super(r, g, b);
    }

    public Color(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    public Color(ColorSpace cspace, float[] components, float alpha) {
        super(cspace, components, alpha);
    }

    public Color scaleAlpha(double alphaRatio) {
        return new Color(this.getRed(), this.getGreen(), this.getBlue(), (int) (this.getAlpha() * alphaRatio));
    }

    public static Color transition(Color from, Color to, double ratio) {
        float red = (float)Math.abs((ratio * to.getRed()) + ((1 - ratio) * from.getRed()));
        float green = (float)Math.abs((ratio * to.getGreen()) + ((1 - ratio) * from.getGreen()));
        float blue = (float)Math.abs((ratio * to.getBlue()) + ((1 - ratio) * from.getBlue()));
        float alpha = (float)Math.abs((ratio * to.getAlpha()) + ((1 - ratio) * from.getAlpha()));

        return new Color(red/255, green/255, blue/255, alpha/255);
    }
}

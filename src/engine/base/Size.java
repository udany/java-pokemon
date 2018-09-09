package engine.base;

public class Size {
    public int width;
    public int height;

    public Size() {
    }

    public Size(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public void set(int w, int h) {
        this.width = w;
        this.height = h;
    }
}

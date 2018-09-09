package engine.window;

import Pokemon.objects.input.GalaxyInput;
import engine.base.Size;
import engine.input.GenericInput;
import engine.input.Keyboard;
import engine.main.GameObject;
import engine.main.Stage;
import engine.util.Event;
import engine.util.MyFrame;
import engine.window.Transition.Transition;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class Game extends MyFrame {
    public Size size = new Size();
    private Color background;
    private Canvas canvas;

    protected Keyboard keyboard = Keyboard.getInstance();

    public Event<Graphics2D> onPaint = new Event<>();
    public Event<Double> onUpdate = new Event<>();

    public Game(int w, int h){
        super();

        background = Color.BLACK;

        setIgnoreRepaint( true );
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        size.height = h;
        size.width = w;
        setSize(size.width, size.height);
        setResizable(false);
        centerOnScreen();

        // Create canvas for painting...
        canvas = new Canvas();
        canvas.setIgnoreRepaint( true );
        canvas.setSize( size.width, size.height);

        // Add canvas to game window...
        add(canvas);
        pack();
        setVisible(true);

        // Setup a base stage
        baseStage = new Stage();
        addStage(baseStage);
    }

    protected int frameRate = 120;
    protected BufferStrategy buffer;
    protected Graphics2D g2d;
    protected Graphics graphics;
    protected BufferedImage bufferImage;
    protected double fps;
    protected boolean debug = false;
    protected int msPerFrame;
    protected GraphicsEnvironment ge;
    protected GraphicsDevice gd;
    protected GraphicsConfiguration gc;

    private void setup() {
        canvas.createBufferStrategy( 2 );
        buffer = canvas.getBufferStrategy();

        // Get graphics configuration...
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gd = ge.getDefaultScreenDevice();
        gc = gd.getDefaultConfiguration();

        // Create off-screen drawing surface
        bufferImage = gc.createCompatibleImage( size.width, size.height);

        msPerFrame = 1000/frameRate;
    }

    abstract public GenericInput getInput();

    long gameTime;
    public long getGameTime() { return gameTime; }

    public void start() {
        setup();

        // Variables for counting frames per seconds
        int frames = 0;
        long elapsed = 0;
        gameTime = System.currentTimeMillis();

        while( true ) {
            try {
                // count Frames per second...
                elapsed = System.currentTimeMillis() - gameTime;
                if (elapsed >= msPerFrame) {
                    gameTime = System.currentTimeMillis();

                    fps = 1000 / elapsed;

                    // clear back buffer...
                    g2d = bufferImage.createGraphics();
                    update(elapsed);
                    draw(g2d);
                    onPaint.emit(g2d);
                    g2d.dispose();

                    // Blit image and flip...
                    graphics = buffer.getDrawGraphics();
                    graphics.drawImage(bufferImage, 0, 0, null );
                    if( !buffer.contentsLost() )
                        buffer.show();
                }

                // Let the OS have a little time...
                Thread.yield();
            } finally {
                // release resources
                if( graphics != null )
                    graphics.dispose();
                if( g2d != null )
                    g2d.dispose();
            }
        }
    }

    Stage baseStage;
    protected List<Stage> stages = new ArrayList<>();
    protected List<Stage> stagesToAdd = new ArrayList<>();
    protected List<Stage> stagesToRemove = new ArrayList<>();
    public void addStage(Stage s) { stagesToAdd.add(s); }
    public void removeStage(Stage s) { stagesToRemove.add(s); }
    public void clearStages(Stage s) {
        stagesToRemove.addAll(stages);
        stagesToAdd.add(baseStage);
    }

    protected void draw(Graphics2D graphics){
        graphics.setColor( background );
        graphics.fillRect( 0, 0, size.width, size.height);

        AffineTransform baseTransform = graphics.getTransform();
        for (Stage stage : stages) {
            stage.draw(graphics);
        }

        if (currentTransition != null) {
            currentTransition.draw(graphics);
        }

        if (debug) {
            graphics.setFont( new Font( "Courier New", Font.PLAIN, 12 ) );
            graphics.setColor( Color.GREEN );
            graphics.drawString( String.format( "FPS: %s", fps ), 20, 20 );

            // tree.draw(graphics);
        }
    }

    protected void update(long elapsedMs){
        stages.removeAll(stagesToRemove);
        stagesToRemove.forEach(stage -> stage.onRemove.emit(this));
        stagesToRemove.clear();

        stages.addAll(stagesToAdd);
        stagesToAdd.forEach(stage -> stage.onAdd.emit(this));
        stagesToAdd.clear();

        for (Stage stage : stages) {
            stage.update(elapsedMs);
        }

        onUpdate.emit(((double)elapsedMs) / 1000);
    }

    Transition currentTransition;
    public void transition(Transition t) {
        currentTransition = t;
        if (t == null) return;

        t.onEnd.addListener(x -> {
            if (currentTransition != null)
            transition(currentTransition.next());
        });

        currentTransition.start();
    }

    public void addObject(GameObject obj) {
        baseStage.addObject(obj);
    }
}

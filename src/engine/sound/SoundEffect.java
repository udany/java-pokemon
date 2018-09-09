package engine.sound;

import engine.resources.ResourceLoader;
import engine.util.Event;

import javax.sound.sampled.*;

public class SoundEffect {
    private SoundData sound;

    public DataLine.Info info;
    public SourceDataLine line;

    public Event onStart = new Event();
    public Event onStop = new Event();

    public SoundEffect(String file) {

        try {
            sound = ResourceLoader.loadSound(file);

            info = new DataLine.Info(SourceDataLine.class, sound.format);

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(sound.format);
        } catch (Exception e) {
            System.out.println("Failed loading sound " + file);
        }
    }

    private boolean isPlaying = false;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void start() {
        if (isPlaying) return;
        isPlaying = true;

        new Thread(() -> {
            line.start();
            onStart.emit();
            int size = sound.BUFFER_SIZE;
            for (int i = 0; i < sound.data.length; i += size) {
                if (!isPlaying()) break;

                if (i + sound.BUFFER_SIZE >= sound.data.length) {
                    size = sound.data.length - i;
                }
                line.write(sound.data, i, size);
            }
            line.drain();
            line.stop();

            isPlaying = false;
            onStop.emit();
        }).start();
    }

    public void stop() {
        isPlaying = false;
    }

    public double getVolume() {
        FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        return Math.pow(10f, gainControl.getValue() / 20f);
    }

    public SoundEffect setVolume(double volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));

        return this;
    }
}

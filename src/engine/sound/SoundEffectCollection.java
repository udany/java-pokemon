package engine.sound;

import java.util.ArrayList;
import java.util.List;

public class SoundEffectCollection {

    private List<SoundEffect> sounds;
    private int playbackMode;
    private static final int SEQUENTIAL = 1;
    private int current;

    public SoundEffectCollection(String... files){
        this(SEQUENTIAL, files);
    }
    public SoundEffectCollection(int mode, String... files){
        playbackMode = mode;
        sounds = new ArrayList<>();

        for (String f : files){
            sounds.add(new SoundEffect(f));
        }
        current = 0;
    }

    public void start(){
        sounds.get(current).start();

        current = (current+1) % sounds.size();
    }

    public void setVolume(double volume) {
        for (SoundEffect s : sounds)
            s.setVolume(volume);
    }
}

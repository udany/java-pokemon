package engine.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SoundEffectPool {
    private String file;
    private List<SoundEffect> soundEffects = new ArrayList<>();
    private double volume = 1;

    public SoundEffectPool(String file) {
        this.file = file;

        newSoundEffect();
    }

    private SoundEffect newSoundEffect() {
        SoundEffect sfx = new SoundEffect(this.file);
        sfx.setVolume(volume);
        soundEffects.add(sfx);
        return sfx;
    }

    public SoundEffect get() {
        List<SoundEffect> available = soundEffects.stream().filter(x -> !x.isPlaying()).collect(Collectors.toList());
        if (available.size() > 0) {
            return available.get(0);
        } else {
            return newSoundEffect();
        }
    }

    public double getVolume(){
        return volume;
    }

    public SoundEffectPool setVolume(double vol) {
        volume = vol;
        for (SoundEffect soundEffect : soundEffects) {
            soundEffect.setVolume(volume);
        }

        return this;
    }
}

package engine.sound;

import engine.resources.ResourceLoader;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundData {
    public int BUFFER_SIZE = 256;

    public AudioFormat format;
    public byte[] data;
    public DataLine.Info info;
    public SourceDataLine line;

    public SoundData(String file) throws Exception {
        InputStream in = ResourceLoader.getResourceUrl(file).openStream();

        // The streams needs to be mark enabled
        InputStream bufferedIn = new BufferedInputStream(in);

        AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
        format = ais.getFormat();

        data = new byte[(int) ais.getFrameLength() * format.getFrameSize()];

        byte[] buf = new byte[BUFFER_SIZE];

        for (int i = 0; i < data.length; i += BUFFER_SIZE) {
            int r = ais.read(buf, 0, BUFFER_SIZE);

            if (i + r >= data.length) {
                r = data.length - i;
            }

            System.arraycopy(buf, 0, data, i, r);
        }
        ais.close();
    }
}

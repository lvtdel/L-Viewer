package OS;

import javax.sound.sampled.AudioFormat;

public class Audio {
    public static AudioFormat GetAudioFormat() {
        float sampleRate = 8000.0f;
        int sampleSizeInBits = 16;
        boolean signed = true;
        int channels = 2;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}

import javax.sound.sampled.AudioFormat;

class AFormat {

    AudioFormat audioFormat;
    float sampleRate;
    int sampleSizeInBits, channels;
    boolean signed, bigEndian;

    // getAudio Format
    public AudioFormat getAudioFormat() {
        sampleRate = 16000.0F;
        sampleSizeInBits = 16;
        channels = 2;
        signed = true;
        bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

}

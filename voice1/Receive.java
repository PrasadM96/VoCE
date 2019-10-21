
//package voice;

import java.net.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

class Receive extends AFormat implements Runnable {

    private DatagramSocket socket = null;
    private int packetsize = 1000;
    private int port = 7000;
    private DatagramPacket packet = null;
    private SourceDataLine sourceDataLine;

    public void playAudio() {
        try {
            audioFormat = this.getAudioFormat(); // get the audio format

            DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            // //Setting the maximum volume
            FloatControl control = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(control.getMaximum() / 2);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    private void play() {
        try {

            this.socket = new DatagramSocket(port);

            this.packet = new DatagramPacket(new byte[this.packetsize], this.packetsize);
            this.playAudio();
            for (;;) {
                try {

                    this.socket.receive(packet);
                    this.sourceDataLine.write(this.packet.getData(), 0, this.packetsize);
                    System.out.println("receive");

                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            this.socket.close();
        }

    }

    @Override
    public void run() {
        this.play();

    }

}

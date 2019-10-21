import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

class Receive extends AFormat implements Runnable {

    private final int packetsize = 1000;
    private final int port = 3001;
    private boolean stopPlay = false;
    private MulticastSocket socket = null;
    private InetAddress host = null;
    private SourceDataLine sourceDataLine;
    private byte temp[] = new byte[4];

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

    @Override
    public void run() {

        try {
            // Construct the socket
            this.socket = new MulticastSocket(this.port);
            this.socket.joinGroup(this.host);
            this.socket.setLoopbackMode(true);
            System.out.println("The server is ready");

            // Create a packet
            DatagramPacket packet = new DatagramPacket(new byte[this.packetsize], (this.packetsize));
            this.playAudio();

            while (true) {
                if (!this.stopPlay) {
                    try {
                        this.socket.receive(packet);

                        // Print the packet
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());
                        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                        dataInputStream.read(temp, 0, 1);
                        System.out.println(java.nio.ByteBuffer.wrap(temp).getInt());

                        // playing the audio
                        this.sourceDataLine.write(packet.getData(), 0, this.packetsize);

                        packet.setLength(this.packetsize);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.socket.close();
        }
    }

    public Receive(InetAddress host) {
        this.host = host;
    }

    public void stopPlay() {
        this.stopPlay = true;
    }

    public void startPlay() {
        this.stopPlay = false;
    }
}

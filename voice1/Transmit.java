
//package voice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

class Transmit extends Capture implements Runnable {

    private final int size = 1000;
    byte tempBuffer[] = new byte[this.size];
    private ByteArrayOutputStream byteArrayOutputStream;
    private boolean stopCapture;
    private final int port;
    private DatagramSocket socket;
    private final InetAddress hostName;
    private DatagramPacket packet;

    private Transmit(String host) throws UnknownHostException {
        this.port = 3001;
        this.byteArrayOutputStream = null;
        this.socket = null;
        this.stopCapture = false;
        this.hostName = InetAddress.getByName(host);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("usage: java Transmit <host ip>");
            return;
        }

        try {
            Thread transmit = new Thread(new Transmit(args[0]));
            transmit.start();

            Thread play = new Thread(new Receive());
            play.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send() {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.stopCapture = false;
        try {
            int readCount;
            while (!this.stopCapture) {
                readCount = this.getTargetDataLine().read(this.tempBuffer, 0, this.tempBuffer.length); // capture sound
                                                                                                       // into
                                                                                                       // tempBuffer
                if (readCount > 0) {
                    this.byteArrayOutputStream.write(this.tempBuffer, 0, readCount);
                    // this.getSourceDataLine().write(tempBuffer, 0, 500);
                    this.packet = new DatagramPacket(this.tempBuffer, this.tempBuffer.length, this.hostName, 7000);
                    socket.send(this.packet);
                }
            }
            this.byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void run() {
        try {
            this.socket = new DatagramSocket(this.port);
            this.captureAudio();
            this.send();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

}

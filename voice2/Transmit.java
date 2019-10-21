import java.net.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

class Transmit extends Capture implements Runnable {

    private final int packetsize = 1000;
    private final int packetsize2 = 5004;
    private final int port = 3000;
    private InetAddress host = null;
    private MulticastSocket socket = null;
    private byte tempBuffer[] = new byte[this.packetsize];
    private boolean stopCapture = true;
    private int seq = 0;

    public Transmit(InetAddress host) {
        this.host = host;
    }

    public Transmit() {
        super();
    }

    private void send() {
        try {
            int readCount;
            while (true) {
                System.out.print("");
                if (!(this.stopCapture)) {

                    readCount = getTargetDataLine().read(this.tempBuffer, 0, this.tempBuffer.length); // capture sound

                    if (readCount > 0) {
                        seq = seq % 16;

                        // include the sequence number in datagram socket
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.packetsize);
                        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                        dataOutputStream.writeInt(seq++);
                        dataOutputStream.write(this.tempBuffer);

                        // create the datagram packet
                        DatagramPacket packet = new DatagramPacket(byteArrayOutputStream.toByteArray(),
                                byteArrayOutputStream.toByteArray().length, this.host, 3001);

                        // Send the packet
                        this.socket.send(packet);

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // stop caturing
    public void stopCapture() {
        this.stopCapture = true;
    }

    // stop caturing
    public void startCapture() {
        this.stopCapture = false;
    }

    public void run() {
        try {
            this.socket = new MulticastSocket();
            this.socket.joinGroup(this.host);
            this.captureAudio();
            this.send();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.socket.close();
        }
    }

    public static void main(String[] args) throws UnknownHostException {

        // Check the whether the arguments are given
        if (args.length != 1) {
            System.out.println("DatagramClient host ");
            return;
        }

        Transmit cap = new Transmit(InetAddress.getByName(args[0]));
        Receive ply = new Receive(InetAddress.getByName(args[0]));

        try {
            // run two threads
            Thread transmit = new Thread(cap);
            transmit.start();
            Thread receive = new Thread(ply);
            receive.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Scanner in = new Scanner(System.in);
        boolean state = true; //

        System.out.println("Press Enter to switch between modes");

        while (true && (cap != null) && (ply != null)) {
            in.nextLine();
            if (state) {
                cap.stopCapture();
                ply.startPlay();
                System.out.println("Playing");
                state = false;
            } else {
                // ply.stopPlay();
                cap.startCapture();
                System.out.println("Capturing");
                state = true;
            }
        }
    }
}

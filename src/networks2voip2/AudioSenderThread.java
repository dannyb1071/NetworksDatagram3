package networks2voip2;

/*
 * TextSender.java
 *
 * Created on 15 January 2003, 15:29
 */
/**
 *
 * @author Danny
 */
import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import java.io.*;
import java.util.*;

public class AudioSenderThread implements Runnable {

    static DatagramSocket sending_socket;
    static AudioRecorder recorder;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {

        //***************************************************
        //Port to send to
        int PORT = 55555;
        //IP ADDRESS to send to
        InetAddress clientIP = null;
        try {
            clientIP = InetAddress.getByName("localhost");  //CHANGE localhost to IP or NAME of client machine
        } catch (UnknownHostException e) {
            System.out.println("ERROR: TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }
        //***************************************************

        //***************************************************
        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.
        //DatagramSocket sending_socket;
        try {
            sending_socket = new DatagramSocket3();
            recorder = new AudioRecorder();
        } catch (SocketException e) {
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        //***************************************************

        //***************************************************
        //Get a handle to the Standard Input (console) so we can read user input
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        //***************************************************

        //***************************************************
        //Main loop.
        ArrayList<DatagramPacket> packets = new ArrayList<>();
        boolean running = true;
        int packetCount = 0;
        int sendCount = 0;

        byte count[];
        int a = 1;
        int total = 0;
        while (running) {
            try {

                /*
            Packet loss testing
            count = new byte[] {(byte)a};
            DatagramPacket packet = new DatagramPacket(count, count.length, clientIP, PORT);
            sending_socket.send(packet);
            total++;
            if(total == 10000){
                running = false;
            }*/



                /*Socket 2*/
                ArrayList<Byte> data = new ArrayList<>();
                byte[] block = recorder.getBlock();
                data.add((byte)packetCount);
                data.add((byte)sendCount);
                for(byte b : block){
                    data.add(b);
                }
//                System.out.println("Send packet " + packetCount + " in " + sendCount);

                byte[] send = new byte[514];
                for(int i = 0; i < 514; i++){
                    send[i] = (byte)data.get(i);
                }
                DatagramPacket packet = new DatagramPacket(send, send.length, clientIP, PORT);

//                sending_socket.send(packet);

                packets.add(packet);
                packetCount++;
                if(packets.size() == 16){
                    for(int i = 0; i < 16; i++){
                        int x = i/4;
                        int y = i%4;
                        int pos = y*4 + (3-x);
                        sending_socket.send(packets.get(pos));
                    }
                    packets.clear();
                    sendCount++;
                }

                if(packetCount == 16){
                    packetCount = 0;
                }
                if(sendCount == 128){
                    sendCount = 0;
                }

            } catch (IOException e) {
                System.out.println("ERROR: TextSender: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket
        recorder.close();
        sending_socket.close();
        //***************************************************
    }
}

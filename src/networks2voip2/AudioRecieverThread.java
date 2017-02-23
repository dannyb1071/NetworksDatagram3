package networks2voip2;

import CMPC3M06.AudioPlayer;
import java.io.BufferedReader;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

import javax.sound.sampled.LineUnavailableException;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Callum on 16/02/2017.
 */
public class AudioRecieverThread implements Runnable {

    static DatagramSocket receiving_socket;
    static AudioPlayer player;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {

        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************

        //***************************************************
        //Open a socket to receive from on port PORT
        //DatagramSocket receiving_socket;
        try {
            receiving_socket = new DatagramSocket3(PORT);
            player = new AudioPlayer();
        } catch (SocketException e) {
            System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        //***************************************************
        //Main loop.
        boolean running = true;
        int recieveCount = 0;
        int num = 0;
        byte[] test = new byte[1];
        DatagramPacket saved = null;
        ArrayList<DatagramPacket> packets = new ArrayList<>();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            try {
                byte[] buffer = new byte[514];
                byte[] playback = new byte[512];
                DatagramPacket[] packs = new DatagramPacket[16];
                DatagramPacket packet = new DatagramPacket(buffer, 0, 514);
                receiving_socket.receive(packet);

                if (packet.getData()[1] == recieveCount) {
                    packets.add(packet);
                } else {
                    for (int i = 0; i < packets.size(); i++) {
                        packs[packets.get(i).getData()[0]] = packets.get(i);
                    }
                    recieveCount++;
                    if (recieveCount == 128) {
                        recieveCount = 0;
                    }
                    packets.clear();
                    packets.add(packet);
                    for (int x = 0; x < packs.length; x++) {
                        if (packs[x] != null) {
//                            System.out.println("True");
                            System.arraycopy(packs[x].getData(), 2, playback, 0, 512);
                            num = packs[x].getData()[0];
//                            System.out.println("Played " + num + " in " + packs[x].getData()[1]);
                            player.playBlock(playback);
                            playback = new byte[512];
                        } else {
                            for (int j = 0; j < 16; j++) {
                                if (j > x) {
                                    System.arraycopy(saved.getData(), 2, playback, 0, 512);
                                    num = saved.getData()[0];
//                                    System.out.println("Played " + num + " in " + saved.getData()[1]);
                                    player.playBlock(playback);
                                    playback = new byte[512];
                                    break;
                                } else if (packs[x - j] != null) {
                                    System.arraycopy(packs[x - j].getData(), 2, playback, 0, 512);
                                    num = packs[x - j].getData()[0];
//                                    System.out.println("Played " +num + " in " + packs[x-j].getData()[1]);
                                    player.playBlock(playback);
                                    playback = new byte[512];
                                    break;
                                }
                            }
                        }

                    }
                    for (int i = 15; i >= 0; i--) {
                        if (packs[i] != null) {
                            saved = packs[i];
//                            System.out.println("Saved " + i);
                            break;
                        }
                    }
                }


            } catch (IOException e) {
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            }
        }
        //Close the socket

        player.close();
        receiving_socket.close();
        //***************************************************
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworksDatagram3;

/**
 *
 * @author Danny
 */
public class NetworksDatagram3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AudioRecieverThread r = new AudioRecieverThread();
        AudioSenderThread s = new AudioSenderThread();
        r.start();
        s.start();
    }
    
}

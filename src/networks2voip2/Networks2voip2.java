/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networks2voip2;

/**
 *
 * @author Danny
 */
public class Networks2voip2 {

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

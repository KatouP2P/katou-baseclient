package ga.nurupeaches.katou.network.server;

import ga.nurupeaches.katou.Configuration;
import junit.framework.TestCase;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class CombinedServerTest extends TestCase {

    private Server tcpServer, udpServer;
    private static final int PORT = 8080;

    // Not a list of waifus, I swear!
    private String[] randomStrings = {
            "Sejuani", "Tenshi", "Frau", "Xenovia", "Katou", "Aqua", "Yurippe", "Origami",
            "Tohsaka", "Pepperoni", "Schokolade", "Sarasvati", "Maki", "Chizuru", "Galil"
    };

    @Override
    protected void setUp() throws Exception {
        tcpServer = new TCPServer(PORT);
        udpServer = new UDPServer(PORT);
    }

    @Override
    protected void tearDown() throws Exception {
        tcpServer.close();
        udpServer.close();
        Configuration.saveConfig();
    }

    @Test
    public void testTick() throws Exception {
        Thread tcpThread = new Thread(() -> {
            try {
                while(true){
                    tcpServer.tick();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        tcpThread.start();

        Thread udpThread = new Thread(() -> {
            try {
                while(true){
                    udpServer.tick();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        udpThread.start();

        try {
            DatagramSocket[] udpSockets = {
                    new DatagramSocket(),
                    new DatagramSocket(),
                    new DatagramSocket(),
            };

            Socket[] tcpSockets = {
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
            };

            Random random = new Random();
            byte[][] strings = new byte[3][];
            for(int i=0; i < strings.length; i++){
                StringBuilder builder = new StringBuilder("Katou");
                for(int x=0; x < 3; x++){
                    builder.append(randomStrings[random.nextInt(randomStrings.length)]);
                }

                strings[i] = builder.toString().getBytes(StandardCharsets.UTF_8);
            }

            int u = 0;
            for(DatagramSocket socket : udpSockets){
                socket.send(new DatagramPacket(strings[u], strings[u].length, InetAddress.getLocalHost(), PORT));
                u++;
            }

            int t = 0;
            for(Socket socket : tcpSockets){
                socket.getOutputStream().write(strings[t]);
                t++;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        synchronized(this){
            this.wait();
        }
    }

}

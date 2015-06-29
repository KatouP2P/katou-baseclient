package ga.nurupeaches.katou.network;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.network.server.Server;
import ga.nurupeaches.katou.network.server.UDPServer;
import junit.framework.TestCase;
import org.junit.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class UDPServerTest extends TestCase {

    private Server server;
    private static final int PORT = 8080;

    // Not a list of waifus, I swear!
    private String[] randomStrings = {
            "Sejuani", "Tenshi", "Frau", "Xenovia", "Katou", "Aqua", "Yurippe", "Origami",
            "Tohsaka", "Pepperoni", "Schokolade", "Sarasvati", "Maki", "Chizuru", "Galil"
    };

    @Override
    protected void setUp() throws Exception {
        server = new UDPServer(PORT);
    }

    @Override
    protected void tearDown() throws Exception {
        server.close();
        Configuration.saveConfig();
    }

    @Test
    public void testTick() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                while(true){
                    server.tick();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        thread.start();

        try {
            DatagramSocket[] sockets = {
                    new DatagramSocket(),
                    new DatagramSocket(),
                    new DatagramSocket(),
            };

            Random random = new Random();
            for(DatagramSocket socket : sockets){
                StringBuilder builder = new StringBuilder();
                for(int i=0; i < 20; i++){
                    builder.append(randomStrings[random.nextInt(randomStrings.length)]);
                }

                byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(new byte[]{57}, 1, InetAddress.getLocalHost(), PORT));
                socket.send(new DatagramPacket(bytes, bytes.length, InetAddress.getLocalHost(), PORT));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        synchronized(this){
            this.wait();
        }
    }

}

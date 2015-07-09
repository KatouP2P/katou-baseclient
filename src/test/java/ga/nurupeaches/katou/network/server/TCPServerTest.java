package ga.nurupeaches.katou.network.server;

import ga.nurupeaches.katou.Configuration;
import junit.framework.TestCase;
import org.junit.Test;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class TCPServerTest extends TestCase {

    private Server server;
    private static final int PORT = 8080;

    // Not a list of waifus, I swear!
    private String[] randomStrings = {
            "Sejuani", "Tenshi", "Frau", "Xenovia", "Katou", "Aqua", "Yurippe", "Origami",
            "Tohsaka", "Pepperoni", "Schokolade", "Sarasvati", "Maki", "Chizuru", "Galil"
    };

    @Override
    protected void setUp() throws Exception {
        server = new TCPServer(PORT);
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
            Socket[] sockets = {
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
                    new Socket(InetAddress.getLocalHost(), PORT),
            };

            Random random = new Random();
            for(Socket socket : sockets){
                StringBuilder builder = new StringBuilder("Katou");
                for(int i=0; i < 4; i++){
                    builder.append(randomStrings[random.nextInt(randomStrings.length)]);
                }

                socket.getOutputStream().write(builder.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        synchronized(this){
            this.wait();
        }
    }

}

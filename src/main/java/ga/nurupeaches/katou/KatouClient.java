package ga.nurupeaches.katou;

import ga.nurupeaches.katou.network.server.Server;
import ga.nurupeaches.katou.network.server.TCPServer;
import ga.nurupeaches.katou.network.server.UDPServer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KatouClient {

    /**
     * Global logger.
     */
    public static final Logger LOGGER = Logger.getLogger("Katou");

    private Server tcp, udp;

    public static void main(String... args){
        KatouClient client = new KatouClient();
        client.setupNetworking();
    }

    public void setupNetworking(){
        try {
            tcp = new TCPServer(Configuration.getPort());
            udp = new UDPServer(Configuration.getPort());
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "Failed to initialize TCP or UDP servers!", e);
        }
    }

    public void tick(){
        try {
            tcp.tick();
        } catch (Exception e){
            LOGGER.log(Level.SEVERE, "tcp tick failed", e);
        }

        try {
            udp.tick();
        } catch (Exception e){
            LOGGER.log(Level.SEVERE, "udp tick failed", e);
        }
    }

}

package ga.nurupeaches.katou.network;

import ga.nurupeaches.katou.network.server.Server;
import ga.nurupeaches.katou.network.server.TCPServer;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class NonJUnitTCPServerTest {

    public static final int PORT = 8080;

    private NonJUnitTCPServerTest(){}

    public static void main(String[] args) throws Exception {
        Server server = new TCPServer(PORT);
        Thread thread = new Thread(() -> {
            while(true){
                server.tick();
            }
        });
        thread.start();

        // Simulate multiple clients.
        Socket[] sockets = {
                new Socket(InetAddress.getLocalHost(), PORT),
                new Socket(InetAddress.getLocalHost(), PORT),
                new Socket(InetAddress.getLocalHost(), PORT),
        };
        Scanner scanner = new Scanner(System.in);
        String l;
        while((l = scanner.nextLine()) != null){
            for(Socket socket : sockets)
                socket.getOutputStream().write(l.getBytes(StandardCharsets.UTF_8));
        }
    }

}

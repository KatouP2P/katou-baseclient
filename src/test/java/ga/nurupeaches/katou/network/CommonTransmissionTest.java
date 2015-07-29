package ga.nurupeaches.katou.network;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.chunk.MemoryChunk;
import ga.nurupeaches.katou.filesystem.KatouDirectory;
import ga.nurupeaches.katou.filesystem.KatouFile;
import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.peer.PeerConnection;
import ga.nurupeaches.katou.network.server.Server;
import ga.nurupeaches.katou.network.server.TCPServer;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

public class CommonTransmissionTest extends TestCase {

    public static final TestType TEST_TYPE = TestType.DIRECTORY; // Change to test different cases.

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
    }

    @Override
    protected void tearDown() throws Exception {
        server.close();
        Configuration.saveConfig();
    }

    @Test
    public void testTick() throws Exception {
        Peer peer = new Peer();
        AsynchronousSocketChannel socket = AsynchronousSocketChannel.open();
        socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), PORT));
        peer.connection = new PeerConnection(socket, socket.getLocalAddress(), peer);

        // write client version/identifier
        socket.write(ByteBuffer.wrap("KatouTestClient".getBytes(StandardCharsets.UTF_8)));
        // TODO: implement ping-back saying the server has accepted us; Thread.sleep(50) is a temporary workaround.
        Thread.sleep(50);

        executeTransmission(peer);

        synchronized(this){
            this.wait();
        }
    }

    public void executeTransmission(Peer peer) throws Exception {
        switch(TEST_TYPE){
            case DIRECTORY:
                KatouDirectory dir = new KatouDirectory(new File("C:/Users/Tsunko/KatouTestingGrounds"));
                dir.transferTo(peer);
                break;

            case FILE:
                KatouFile kFile;
                for(File file : new File("C:\\Users\\Tsunko\\KatouTestingGrounds").listFiles()){
                    if(file.isDirectory()) continue;
                    kFile = new KatouFile(file);
                    kFile.transferTo(peer);
                }
                break;

            case MEMORY_CHUNK:
                MemoryChunk chunk = new MemoryChunk(1337, 256);
                chunk.transferTo(peer);
        }
    }

    private enum TestType {
        DIRECTORY, FILE, MEMORY_CHUNK
    }

}

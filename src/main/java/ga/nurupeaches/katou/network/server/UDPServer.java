package ga.nurupeaches.katou.network.server;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.peer.PeerConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listens and accepts incoming UDP "connections".
 */
public class UDPServer implements Server {

    private final int MAX_THREAD_COUNT = Runtime.getRuntime().availableProcessors()/2;
    private final AtomicInteger POLLING_COUNT = new AtomicInteger(0);
    private ExecutorService datagramThreadService = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    private DatagramChannel datagramChannel;

    public UDPServer(int port) throws IOException{
        datagramChannel = DatagramChannel.open().bind(new InetSocketAddress(port));
        datagramChannel.configureBlocking(false);
        datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
    }

    @Override
    public void tick(){
        if(POLLING_COUNT.get() < MAX_THREAD_COUNT){
            datagramThreadService.submit(() -> {
                ByteBuffer anonRecvBuffer = ByteBuffer.allocate(Configuration.getRecvBufferSize());
                try {
                    SocketAddress address = datagramChannel.receive(anonRecvBuffer);
                    if(address == null){
                        return;
                    }

                    System.out.println("[thread-" + Thread.currentThread().getId() + "] Accepted new connection from "
                            + address + ". Authenticating...");
                    String ver = new String(anonRecvBuffer.array(), StandardCharsets.UTF_8);
                    if(ver.isEmpty() || !ver.startsWith("Katou")){
                        System.out.println("[thread-" + Thread.currentThread().getId() + "] Non-Katou client tried to " +
                                "connect from " + address);
                    } else {
                        DatagramChannel peerChannel;
                        peerChannel = DatagramChannel.open();
                        peerChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
//                      peerChannel.configureBlocking(false);
                        peerChannel.connect(address);

                        Peer peer = new Peer();
                        peer.connection = new PeerConnection(peerChannel, address, peer);
                        System.out.println("[thread-" + Thread.currentThread().getId() + "] Successful authentication " +
                                "from " + peer.connection.getAddress() + " using client " + ver);
                    }
                } catch (IOException e){
                    e.printStackTrace(); // TODO: Handle better
                } finally {
                    POLLING_COUNT.decrementAndGet();
                    anonRecvBuffer.clear();
                }
            });
        }
    }

    @Override
    public void close() throws IOException {
        datagramThreadService.shutdownNow();
        try {
            datagramThreadService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e){
            throw new IOException(e);
        }
    }

}

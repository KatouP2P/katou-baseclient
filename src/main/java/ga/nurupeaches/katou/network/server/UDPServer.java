package ga.nurupeaches.katou.network.server;

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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listens and accepts incoming UDP "connections".
 */
public class UDPServer implements Server {

    private final int MAX_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    private final AtomicInteger POLLING_COUNT = new AtomicInteger(0);
    private final Object LOCK_OBJECT = new Object();
    private final ExecutorService datagramThreadService = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
            new NamedForkJoinWorkerThreadFactory("udp-thread-"), null, true);
    private final DatagramChannel datagramChannel;

    public UDPServer(int port) throws IOException{
        datagramChannel = DatagramChannel.open().bind(new InetSocketAddress(port));
        datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
    }

    @Override
    public void tick(){
        if(POLLING_COUNT.get() < MAX_THREAD_COUNT){
            datagramThreadService.submit(() -> {
                ByteBuffer anonRecvBuffer = ByteBuffer.allocate(32);
                try {
                    SocketAddress address = datagramChannel.receive(anonRecvBuffer);
                    if(address == null){
                        return;
                    }

                    System.out.println('[' + Thread.currentThread().getName() + "] Accepted new connection from "
                            + address + ". Authenticating...");
                    String ver = new String(anonRecvBuffer.array(), StandardCharsets.UTF_8);
                    if(ver.isEmpty() || !ver.startsWith("Katou")){
                        System.out.println('[' + Thread.currentThread().getName() + "] Non-Katou client tried to " +
                                "connect from " + address + ";dbg,ver=" + ver);
                    } else {
                        DatagramChannel peerChannel;
                        peerChannel = DatagramChannel.open();
                        peerChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
//                      peerChannel.configureBlocking(false);
                        peerChannel.connect(address);

                        Peer peer = new Peer();
                        peer.connection = new PeerConnection(peerChannel, address, peer);
                        System.out.println('[' + Thread.currentThread().getName() + "] Successful authentication " +
                                "from " + peer.connection.getAddress() + " using client " + ver);
                    }
                } catch (IOException e){
                    e.printStackTrace(); // TODO: Handle better
                } finally {
                    POLLING_COUNT.decrementAndGet();
                    anonRecvBuffer.clear();

                    synchronized(LOCK_OBJECT){
                        LOCK_OBJECT.notify();
                    }
                }
            });

            POLLING_COUNT.incrementAndGet();
        } else {
            synchronized(LOCK_OBJECT){
                try {
                    LOCK_OBJECT.wait();
                } catch (InterruptedException e){
                    e.printStackTrace(); // TODO: Handle
                }
            }
        }
    }

    @Override
    public ExecutorService getService(){
        return datagramThreadService;
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

package ga.nurupeaches.katou.network.server;

import ga.nurupeaches.katou.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
    }

    @Override
    public void tick(){
        if(POLLING_COUNT.get() < MAX_THREAD_COUNT){
            datagramThreadService.submit(() -> {
                ByteBuffer anonRecvBuffer = ByteBuffer.allocate(Configuration.getRecvBufferSize());
                SocketAddress address;

                try {
                    address = datagramChannel.receive(anonRecvBuffer);
                } catch(IOException e){
                    e.printStackTrace(); // TODO: Handle better
                    POLLING_COUNT.decrementAndGet();
                    return;
                }

                if(address == null){
                    return;
                }

                System.out.println(address + " says: " + new String(anonRecvBuffer.array(), StandardCharsets.UTF_8));
                anonRecvBuffer.clear();
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

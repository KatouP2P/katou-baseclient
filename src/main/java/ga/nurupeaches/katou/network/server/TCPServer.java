package ga.nurupeaches.katou.network.server;

import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.server.handlers.NewConnectionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listens and accepts incoming TCP connections.
 */
public class TCPServer implements Server {

    private final AsynchronousServerSocketChannel serverSocketChannel;
    private final AtomicInteger THREAD_POOL_COUNT = new AtomicInteger(0);
    private final Object LOCK_OBJECT = new Object();
    private final AsynchronousChannelGroup channelThreadGroup = AsynchronousChannelGroup.withFixedThreadPool(
            Runtime.getRuntime().availableProcessors(), runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("tcp-thread-" + THREAD_POOL_COUNT.getAndIncrement());
                return thread;
            }
    );

    public TCPServer(int port) throws IOException {
        serverSocketChannel = AsynchronousServerSocketChannel.open(channelThreadGroup).bind(new InetSocketAddress(port));
    }

    @Override
    public void tick(){
        serverSocketChannel.accept(new Peer(), new NewConnectionHandler(LOCK_OBJECT));

        synchronized(LOCK_OBJECT){
            try {
                LOCK_OBJECT.wait();
            } catch (InterruptedException e){
                e.printStackTrace(); // TODO: Handle
            }
        }
    }

    @Override
    public void close() throws IOException {
        serverSocketChannel.close();
        channelThreadGroup.shutdownNow();
    }

}

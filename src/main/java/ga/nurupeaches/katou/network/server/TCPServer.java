package ga.nurupeaches.katou.network.server;

import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.server.handlers.NewConnectionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * Listens and accepts incoming TCP connections.
 */
public class TCPServer implements Server {

    private final AsynchronousServerSocketChannel socketChannel;
    private final Object LOCK_OBJECT = new Object();

    private final ExecutorService socketThreadService = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
            new NamedForkJoinWorkerThreadFactory("tcp-thread-"), null, true);
    private final AsynchronousChannelGroup socketThreadGroup = AsynchronousChannelGroup.withThreadPool(socketThreadService);

    public TCPServer(int port) throws IOException {
        socketChannel = AsynchronousServerSocketChannel.open(socketThreadGroup).bind(new InetSocketAddress(port));
    }

    @Override
    public void tick(){
        socketChannel.accept(new Peer(), new NewConnectionHandler(this, LOCK_OBJECT));

        synchronized(LOCK_OBJECT){
            try {
                LOCK_OBJECT.wait();
            } catch (InterruptedException e){
                e.printStackTrace(); // TODO: Handle
            }
        }
    }

    @Override
    public ExecutorService getService(){
        return socketThreadService;
    }

    @Override
    public void close() throws IOException {
        socketChannel.close();
        socketThreadGroup.shutdownNow();
    }

}

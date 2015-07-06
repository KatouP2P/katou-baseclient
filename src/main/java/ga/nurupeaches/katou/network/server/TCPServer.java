package ga.nurupeaches.katou.network.server;

import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.server.handlers.NewConnectionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * Listens and accepts incoming TCP connections.
 */
public class TCPServer implements Server {

    private final AsynchronousChannelGroup channelThreadGroup = AsynchronousChannelGroup.withFixedThreadPool(
            Runtime.getRuntime().availableProcessors(), Thread::new);
    private final AsynchronousServerSocketChannel serverSocketChannel;
    private final Object LOCK_OBJECT = new Object();


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

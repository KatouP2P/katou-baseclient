package ga.nurupeaches.katou.network.server;

import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.peer.PeerConnection;
import ga.nurupeaches.katou.network.peer.PeerManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * Listens and accepts incoming TCP connections.
 */
public class TCPServer implements Server {

    private final AsynchronousChannelGroup channelThreadGroup = AsynchronousChannelGroup.withFixedThreadPool(
            Runtime.getRuntime().availableProcessors() / 2, Thread::new);
    private final AsynchronousServerSocketChannel serverSocketChannel;
    private final Object LOCK_OBJECT = new Object();

    public TCPServer(int port) throws IOException {
        serverSocketChannel = AsynchronousServerSocketChannel.open(channelThreadGroup).bind(new InetSocketAddress(port));
    }

    @Override
    public void tick(){
        serverSocketChannel.accept(new Peer(), new CompletionHandler<AsynchronousSocketChannel, Peer>() {

            @Override
            public void completed(AsynchronousSocketChannel channel, Peer peer){
                peer.connection = new PeerConnection(channel, peer);
                PeerManager.get().registerPeer(peer);
                System.out.println("Accepted new connection and registered peer from " + peer.connection.getAddress());
                channel.read(peer.buffer, peer, new ReadCompletionHandler());

                synchronized(LOCK_OBJECT){
                    LOCK_OBJECT.notifyAll();
                }
            }

            @Override
            public void failed(Throwable throwable, Peer peer){
                throwable.printStackTrace(); // TODO: Handle
            }

        });

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

    private class ReadCompletionHandler implements CompletionHandler<Integer, Peer> {

        @Override
        public void completed(Integer numBytesRead, Peer peer){
            if(numBytesRead == -1){
                peer.connection.disconnect();
            } else {
                peer.buffer.flip(); // For the love of god, never forget to call flip().

                byte[] b = new byte[numBytesRead];
                peer.buffer.get(b);
                // TODO: do something with data rather than just outputting what we got
                System.out.println(peer.connection.getAddress() + " says: " + new String(b, StandardCharsets.UTF_8));
                peer.buffer.clear();

                peer.connection.getChannel().read(peer.buffer, peer, this);
            }
        }

        @Override
        public void failed(Throwable exc, Peer attachment){
            exc.printStackTrace(); // TODO: Handle
        }
    }

}

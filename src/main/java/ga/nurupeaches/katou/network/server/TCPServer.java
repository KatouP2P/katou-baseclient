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
                try {
                    peer.connection = new PeerConnection(channel, channel.getRemoteAddress(), peer);
                } catch (IOException e){
                    // TODO: handle
                }


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

    private class AuthenticateConnectionHandler implements CompletionHandler<Integer, Peer> {


        @Override
        public void completed(Integer numBytesRead, Peer peer){
            PeerManager.get().registerPeer(peer);
            System.out.println("Accepted new connection from " + peer.connection.getAddress() + ". Authenticating...");

            System.out.println("Successful authentication from " + peer.connection.getAddress() + ".");
            ((AsynchronousSocketChannel)peer.connection.getChannel()).read(peer.inBuffer, peer, new ReadCompletionHandler());
        }


        @Override
        public void failed(Throwable exc, Peer attachment){
            exc.printStackTrace(); // TODO: Handle
        }

    }

    private class ReadCompletionHandler implements CompletionHandler<Integer, Peer> {

        @Override
        public void completed(Integer numBytesRead, Peer peer){
            if(numBytesRead == -1){
                peer.connection.disconnect();
            } else {
                peer.inBuffer.flip(); // For the love of god, never forget to call flip().

                byte[] b = new byte[numBytesRead];
                peer.inBuffer.get(b);
                // TODO: do something with data rather than just outputting what we got
                System.out.println(peer.connection.getAddress() + " says: " + new String(b, StandardCharsets.UTF_8));
                peer.inBuffer.clear();

                ((AsynchronousSocketChannel)peer.connection.getChannel()).read(peer.inBuffer, peer, this);
            }
        }

        @Override
        public void failed(Throwable exc, Peer attachment){
            exc.printStackTrace(); // TODO: Handle
        }
    }

}

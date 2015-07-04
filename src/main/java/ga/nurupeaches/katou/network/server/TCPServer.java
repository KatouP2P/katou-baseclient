package ga.nurupeaches.katou.network.server;

import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.peer.PeerConnection;
import ga.nurupeaches.katou.network.peer.PeerManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Listens and accepts incoming TCP connections.
 */
public class TCPServer implements Server {

    // Measured in seconds.
    private final int TIMEOUT = 30;
    private final AsynchronousChannelGroup channelThreadGroup = AsynchronousChannelGroup.withFixedThreadPool(
            Runtime.getRuntime().availableProcessors(), Thread::new);
    private final AsynchronousServerSocketChannel serverSocketChannel;
    private final Object LOCK_OBJECT = new Object();

    public TCPServer(int port) throws IOException {
        serverSocketChannel = AsynchronousServerSocketChannel.open(channelThreadGroup).bind(new InetSocketAddress(port));
    }

    @Override
    public void tick(){
        serverSocketChannel.accept(new Peer(), new NewConnectionHandler());

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

    private class NewConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Peer> {

        @Override
        public void completed(AsynchronousSocketChannel channel, Peer peer){
            ByteBuffer buffer = ByteBuffer.allocate(32);
            try {
                peer.connection = new PeerConnection(channel, channel.getRemoteAddress(), peer);
                channel.read(buffer, TIMEOUT, TimeUnit.SECONDS, peer, new AuthenticateConnectionHandler(channel, buffer));
            } catch(InterruptedByTimeoutException e){
                try {
                    System.out.println("[thread-" + Thread.currentThread().getId() + "] Client from " + channel.getRemoteAddress() + " failed to respond within 30 seconds.");
                    channel.close();
                } catch(IOException e1){
                    // TODO: handle
                }
            } catch(IOException e){
                // TODO: handle
            }

            synchronized(LOCK_OBJECT){
                LOCK_OBJECT.notifyAll();
            }
        }

        @Override
        public void failed(Throwable throwable, Peer peer){
            throwable.printStackTrace(); // TODO: Handle

            synchronized(LOCK_OBJECT){
                LOCK_OBJECT.notifyAll();
            }
        }

    }

    private class AuthenticateConnectionHandler implements CompletionHandler<Integer, Peer> {

        private final ByteBuffer buffer;
        private final AsynchronousSocketChannel channel;

        public AuthenticateConnectionHandler(AsynchronousSocketChannel channel, ByteBuffer buffer){
            this.buffer = buffer;
            this.channel = channel;
        }

        @Override
        public void completed(Integer numBytesRead, Peer peer){
            System.out.println("[thread-" + Thread.currentThread().getId() + "] Accepted new connection from " + peer.connection.getAddress() + ". Authenticating...");

            String version = new String(buffer.array(), StandardCharsets.UTF_8);
            if(version.isEmpty() || !version.startsWith("Katou")){
                System.out.println("[thread-" + Thread.currentThread().getId() + "] Non-Katou client tried to connect from " + peer.connection.getAddress());
                try {
                    channel.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
                return;
            }

            System.out.println("[thread-" + Thread.currentThread().getId() + "] Successful authentication from " + peer.connection.getAddress() + " using client " +
                        new String(buffer.array(), StandardCharsets.UTF_8));

            PeerManager.get().registerPeer(peer);
        }


        @Override
        public void failed(Throwable exc, Peer attachment){
            exc.printStackTrace(); // TODO: Handle

            try {
                channel.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }

}

package ga.nurupeaches.katou.network.server.handlers;

import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.peer.PeerConnection;
import ga.nurupeaches.katou.network.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class NewConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Peer> {

    // Measured in seconds.
    private static final int TIMEOUT = 30;
    private final Object lockingObject;
    private Server server;

    public NewConnectionHandler(Server server, Object lock){
        this.server = server;
        this.lockingObject = lock;
    }

    @Override
    public void completed(AsynchronousSocketChannel channel, Peer peer){
        ByteBuffer buffer = ByteBuffer.allocate(32);

//        server.getService().submit(() -> {
            try {
                peer.connection = new PeerConnection(channel, channel.getRemoteAddress(), peer);
                channel.read(buffer, TIMEOUT, TimeUnit.SECONDS, peer, new AuthenticationHandler(channel, buffer));
            } catch(InterruptedByTimeoutException e){
                try {
                    System.out.println('[' + Thread.currentThread().getName() + "] Client from "
                            + channel.getRemoteAddress() + " failed to respond within 30 seconds.");

                    channel.close();
                } catch(IOException e1){
                    failed(new RuntimeException("Failed to close peer channel", e1), peer);
                }
            } catch(IOException e){
                failed(new RuntimeException("Failed to authenticate peer", e), peer);
            }
//        });

        synchronized(lockingObject){
            lockingObject.notifyAll();
        }
    }

    @Override
    public void failed(Throwable throwable, Peer peer){
        Server.NETWORK_LOGGER.log(Level.WARNING, "Encountered error while handling new connection", throwable);

        synchronized(lockingObject){
            lockingObject.notifyAll();
        }
    }

}

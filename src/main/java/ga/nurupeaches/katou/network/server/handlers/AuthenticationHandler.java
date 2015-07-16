package ga.nurupeaches.katou.network.server.handlers;

import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.peer.PeerManager;
import ga.nurupeaches.katou.network.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class AuthenticationHandler implements CompletionHandler<Integer, Peer> {

    private final ByteBuffer buffer;
    private final AsynchronousSocketChannel channel;

    public AuthenticationHandler(AsynchronousSocketChannel channel, ByteBuffer buffer){
        this.buffer = buffer;
        this.channel = channel;
    }

    @Override
    public void completed(Integer numBytesRead, Peer peer){
        System.out.println('[' + Thread.currentThread().getName() + "] Accepted new connection from "
                + peer.connection.getAddress() + ". Authenticating...");

        String version = new String(buffer.array(), StandardCharsets.UTF_8);
        if(version.isEmpty() || !version.startsWith("Katou")){
            System.out.println('[' + Thread.currentThread().getName() + "] Non-Katou client tried to connect from "
                    + peer.connection.getAddress());

            try {
                channel.close();
            } catch (IOException e){
                Server.NETWORK_LOGGER.log(Level.SEVERE, "Failed to close peer channel", e);
            }
            return;
        }

        System.out.println('[' + Thread.currentThread().getName() + "] Successful authentication from "
                + peer.connection.getAddress() + " using client " + version);

        PeerManager.get().registerPeer(peer);

        ByteBuffer buffer = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES);
        channel.read(buffer, peer, new DataHandler(buffer));
    }


    @Override
    public void failed(Throwable throwable, Peer peer){
        Server.NETWORK_LOGGER.log(Level.WARNING, "Encountered error while authenticating", throwable);

        try {
            channel.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
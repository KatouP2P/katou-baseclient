package ga.nurupeaches.katou.network.server.handlers;

import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.peer.PeerManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

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
                e.printStackTrace();
            }
            return;
        }

        System.out.println('[' + Thread.currentThread().getName() + "] Successful authentication from "
                + peer.connection.getAddress() + " using client " + version);

        PeerManager.get().registerPeer(peer);
        channel.read(peer.IN_BUFFER, peer, new DataHandler(peer.IN_BUFFER));
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
package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.katou.network.peer.iochannel.IOChannel;
import ga.nurupeaches.katou.network.peer.iochannel.TCPChannel;
import ga.nurupeaches.katou.network.peer.iochannel.UDPChannel;
import ga.nurupeaches.katou.network.server.Server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;

public class PeerConnection {

    private IOChannel channel;
    private SocketAddress address;
    private Peer peer;

    public PeerConnection(Channel channel, SocketAddress address, Peer peer){
        if(channel instanceof AsynchronousSocketChannel){
            this.channel = new TCPChannel((AsynchronousSocketChannel)channel);
        } else if(channel instanceof DatagramChannel) {
            this.channel = new UDPChannel((DatagramChannel)channel, address);
        } else {
            throw new IllegalArgumentException("Neither AsynchronousSocketChannel or DatagramChannel!");
        }
        this.peer = peer;
        this.address = address;
    }

    public Channel getRawChannel(){
        return channel._channel();
    }

    public SocketAddress getAddress(){
        return address;
    }

    public void disconnect(){
        try {
            channel.close();
        } catch (IOException e){}

        channel = null;
        address = null;
    }

    public void send(ByteBuffer buffer) throws IOException {
        channel.write(buffer);
    }

    public void recv(ByteBuffer buffer) throws IOException {
        channel.read(buffer);
    }

    public void send(ByteBuffer[] buffers) throws IOException {
        for(ByteBuffer buffer : buffers){
            if(buffer.position() != 0) buffer.flip();
        }

        channel.write(buffers, peer, new CompletionHandler<Long, Peer>() {

            @Override
            public void completed(Long result, Peer peer){
                // Do nothing
            }

            @Override
            public void failed(Throwable exc, Peer peer){
                Server.NETWORK_LOGGER.log(Level.WARNING, "Failed to write data", exc);
            }

        });
    }

    public void recv(ByteBuffer[] buffers) throws IOException {
        for(ByteBuffer buffer : buffers){
            if(buffer.position() != 0) buffer.flip();
        }

        channel.read(buffers, peer, new CompletionHandler<Long, Peer>() {

            @Override
            public void completed(Long result, Peer attachment){
                // Do nothing
            }

            @Override
            public void failed(Throwable exc, Peer attachment){
                Server.NETWORK_LOGGER.log(Level.WARNING, "Failed to read data", exc);
            }

        });


    }

}

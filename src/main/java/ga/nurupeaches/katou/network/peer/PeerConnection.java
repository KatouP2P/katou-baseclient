package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.katou.network.peer.iochannel.IOChannel;
import ga.nurupeaches.katou.network.peer.iochannel.TCPChannel;
import ga.nurupeaches.katou.network.peer.iochannel.UDPChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;

public class PeerConnection {

    private IOChannel channel;
    private SocketAddress address;
    private Peer peer;

    /*
     * TODO: Handle UDP.
     */
    public PeerConnection(Channel channel, SocketAddress address, Peer peer){
        if(channel instanceof AsynchronousSocketChannel){
            this.channel = new TCPChannel((AsynchronousSocketChannel)channel);
        } else {
            this.channel = new UDPChannel((DatagramChannel)channel, address);
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
        channel.send(buffer);
    }

    public void recv(ByteBuffer buffer) throws IOException {
        channel.recv(buffer);
    }

}

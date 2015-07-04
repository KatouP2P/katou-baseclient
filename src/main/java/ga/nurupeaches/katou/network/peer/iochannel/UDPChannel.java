package ga.nurupeaches.katou.network.peer.iochannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPChannel implements IOChannel {

    private DatagramChannel channel;
    private SocketAddress address;

    public UDPChannel(DatagramChannel channel, SocketAddress address){
        this.channel = channel;
        this.address = address;
    }

    @Override
    public void send(ByteBuffer buffer) throws IOException {
        channel.send(buffer, address);
    }

    @Override
    public void recv(ByteBuffer buffer) throws IOException {
        throw new IOException("Can't call recv() on a UDP socket!");
    }

    @Override
    public void close() throws IOException{}

    @Override
    public DatagramChannel _channel(){
        return channel;
    }

}

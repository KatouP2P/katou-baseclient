package ga.nurupeaches.katou.network.peer.iochannel;

import ga.nurupeaches.katou.common.NoFuture;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.Future;

public class UDPChannel implements IOChannel {

    private DatagramChannel channel;
    private SocketAddress address;

    public UDPChannel(DatagramChannel channel, SocketAddress address){
        this.channel = channel;
        this.address = address;
    }

    @Override
    public Future<Integer> send(ByteBuffer buffer) throws IOException {
        return new NoFuture<>(channel.send(buffer, address));
    }

    @Override
    public Future<Integer> recv(ByteBuffer buffer) throws IOException {
        channel.receive(buffer); // recv data
        return new NoFuture<>(buffer.position());   // if i properly implemented this, it should be at 0 before recv
                                                    // and position() should return the amount of data read.
    }

    @Override
    public void close() throws IOException{}

    @Override
    public DatagramChannel _channel(){
        return channel;
    }

}

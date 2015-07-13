package ga.nurupeaches.katou.network.peer.iochannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.DatagramChannel;

public class UDPChannel implements IOChannel {

    private DatagramChannel channel;
    private SocketAddress address;

    public UDPChannel(DatagramChannel channel, SocketAddress address){
        this.channel = channel;
        this.address = address;
    }

    @Override
    public <A> A write(ByteBuffer[] buffers, A attachment, CompletionHandler<Long, ? super A> completionHandler) throws IOException {
        channel.write(buffers);
        return attachment;
    }

    @Override
    public <A> A read(ByteBuffer[] buffers, A attachment, CompletionHandler<Long, ? super A> completionHandler) throws IOException {
        channel.read(buffers); // recv data
        return attachment;   // if i properly implemented this, it should be at 0 before recv
                                                    // and position() should return the amount of data read.
    }

    @Override
    public void close() throws IOException{}

    @Override
    public DatagramChannel _channel(){
        return channel;
    }

}

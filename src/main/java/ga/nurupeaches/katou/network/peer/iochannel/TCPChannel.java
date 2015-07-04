package ga.nurupeaches.katou.network.peer.iochannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class TCPChannel implements IOChannel {

    private AsynchronousSocketChannel channel;

    public TCPChannel(AsynchronousSocketChannel channel){
        this.channel = channel;
    }

    @Override
    public void send(ByteBuffer buffer){
        channel.write(buffer);
    }

    @Override
    public void recv(ByteBuffer buffer){
        channel.read(buffer);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    @Override
    public AsynchronousSocketChannel _channel(){
        return channel;
    }

}

package ga.nurupeaches.katou.network.peer.iochannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

public class TCPChannel implements IOChannel {

    private AsynchronousSocketChannel channel;

    public TCPChannel(AsynchronousSocketChannel channel){
        this.channel = channel;
    }

    @Override
    public Future<Integer> send(ByteBuffer buffer){
        return channel.write(buffer);
    }

    @Override
    public Future<Integer> recv(ByteBuffer buffer){
        return channel.read(buffer);
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

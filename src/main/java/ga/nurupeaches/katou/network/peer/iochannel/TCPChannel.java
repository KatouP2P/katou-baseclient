package ga.nurupeaches.katou.network.peer.iochannel;

import ga.nurupeaches.katou.Configuration;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class TCPChannel implements IOChannel {

    private AsynchronousSocketChannel channel;

    public TCPChannel(AsynchronousSocketChannel channel){
        this.channel = channel;
    }

    @Override
    public <A> A read(ByteBuffer[] buffers, A attachment, CompletionHandler<Long, ? super A> completionHandler) throws IOException {
        channel.write(buffers, 0, buffers.length, Configuration.getTimeout(), TimeUnit.SECONDS, attachment, completionHandler);
        return attachment;
    }

    @Override
    public <A> A write(ByteBuffer[] buffers, A attachment, CompletionHandler<Long, ? super A> completionHandler) throws IOException {
        channel.read(buffers, 0, buffers.length, Configuration.getTimeout(), TimeUnit.SECONDS, attachment, completionHandler);
        return attachment;
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

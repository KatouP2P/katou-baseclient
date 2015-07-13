package ga.nurupeaches.katou.network.peer.iochannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.CompletionHandler;

public interface IOChannel {

    public default void write(ByteBuffer[] buffers) throws IOException {
        write(buffers, null, null);
    }

    public default void read(ByteBuffer[] buffers) throws IOException {
        read(buffers, null, null);
    }

    // I could technically just make this one class, but then DatagramSocket and AsynchronousSocketChannel have no common classes.
    public <A> A read(ByteBuffer[] buffers, A attachment, CompletionHandler<Long, ? super A> completionHandler) throws IOException;

    public <A> A write(ByteBuffer[] buffers, A attachment, CompletionHandler<Long, ? super A> completionHandler) throws IOException;

    public default void close() throws IOException {
        _channel().close();
    }

    public Channel _channel();

}

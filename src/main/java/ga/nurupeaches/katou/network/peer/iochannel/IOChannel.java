package ga.nurupeaches.katou.network.peer.iochannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.util.concurrent.Future;

public interface IOChannel {

    public Future<Integer> send(ByteBuffer buffer) throws IOException;

    public Future<Integer> recv(ByteBuffer buffer) throws IOException;

    public void close() throws IOException;

    public Channel _channel();

}

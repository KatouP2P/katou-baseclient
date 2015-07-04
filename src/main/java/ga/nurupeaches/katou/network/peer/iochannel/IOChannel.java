package ga.nurupeaches.katou.network.peer.iochannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;

public interface IOChannel {

    public void send(ByteBuffer buffer) throws IOException;

    public void recv(ByteBuffer buffer) throws IOException;

    public void close() throws IOException;

    public Channel _channel();

}

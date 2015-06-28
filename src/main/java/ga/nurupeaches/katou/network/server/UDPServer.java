package ga.nurupeaches.katou.network.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class UDPServer implements Server {

    private final DatagramChannel datagramChannel;

    public UDPServer(int port) throws IOException{
        datagramChannel = DatagramChannel.open().bind(new InetSocketAddress(port));
    }

    @Override
    public void tick(){}

    @Override
    public void close() throws IOException {}

}

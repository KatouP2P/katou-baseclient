package ga.nurupeaches.katou.network;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;

public class ReuseAddrDatagramTest {

    public static void main(String[] args) throws Throwable {
        DatagramChannel channel = DatagramChannel.open();
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(new InetSocketAddress(10101));

        DatagramChannel channel2 = DatagramChannel.open();
        channel2.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel2.bind(new InetSocketAddress(10101));


    }

}

package ga.nurupeaches.katou.network;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

public class AsyncInputStreamTest extends TestCase {

    private AsynchronousServerSocketChannel serverChannel;
    private AsynchronousSocketChannel clientChannel;

    @Override
    protected void setUp() throws Exception {
        serverChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(1033));
        clientChannel = AsynchronousSocketChannel.open();
        clientChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 1033));
    }

    @Override
    protected void tearDown() throws Exception {
        serverChannel.close();
        clientChannel.close();
    }

    @Test
    public void test() throws Exception {
        Future<AsynchronousSocketChannel> futureConnection = serverChannel.accept();
        clientChannel.write(ByteBuffer.wrap("Hello, world!".getBytes(StandardCharsets.UTF_8)));

        AsynchronousSocketChannel connectedConnection = futureConnection.get();
        InputStream stream = Channels.newInputStream(connectedConnection);
        int i;
        while((i = stream.read()) != -1){
            System.out.println(i);
        }
        System.out.println("done");
    }

}

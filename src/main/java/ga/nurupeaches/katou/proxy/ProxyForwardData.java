package ga.nurupeaches.katou.proxy;

import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

public class ProxyForwardData implements Transmittable, ProxyAction {

    private SoftReference<Peer> origin;
    private ByteBuffer data;

    public ProxyForwardData(ByteBuffer data){
        this.data = data;

        if(data.position() != 0){
            data.flip();
        }
    }

    public ByteBuffer getData(){
        return data;
    }

    @Override
    public SoftReference<Peer> getOrigin(){
        return origin;
    }

    @Override
    public void transferTo(Peer peer) throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + data.capacity());
        buffer.putInt(data.limit());
        buffer.put(data);
        peer.connection.send(buffer);
    }

    @Override
    public void transferFrom(Peer peer) throws IOException{
        ByteBuffer sizeBuffer = ByteBuffer.allocate(Integer.BYTES);
        peer.connection.recv(sizeBuffer);

        data = ByteBuffer.allocate(sizeBuffer.getInt());
        peer.connection.recv(data);

        origin = new SoftReference<>(peer);
    }

    @Override
    public int getSize() throws IOException{
        return Integer.BYTES + data.capacity();
    }
}

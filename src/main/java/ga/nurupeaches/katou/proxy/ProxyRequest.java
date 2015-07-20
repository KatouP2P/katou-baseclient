package ga.nurupeaches.katou.proxy;

import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.serichan.Transmittable;

import java.lang.ref.SoftReference;
import java.net.Inet4Address;
import java.net.InetSocketAddress;

public class ProxyRequest implements Transmittable, ProxyAction {

    private SoftReference<Peer> origin;
    private boolean isUDP;
    private InetSocketAddress dest;

    public ProxyRequest(){}

    public ProxyRequest(InetSocketAddress dest, boolean isUDP){
        this.dest = dest;
        this.isUDP = isUDP;
    }

    public InetSocketAddress getDest(){
        return dest;
    }

    public boolean isUDP(){
        return isUDP;
    }

    @Override
    public SoftReference<Peer> getOrigin(){
        return origin;
    }

//    @Override
//    public void transferTo(Peer peer) throws IOException{
//        ByteBuffer buffer = ByteBuffer.allocate(Byte.BYTES * 2 + Integer.BYTES + dest.getAddress().getAddress().length);
//        buffer.put((byte)(isUDP ? 1 : 0));
//        buffer.put((byte)(dest.getAddress() instanceof Inet4Address ? 4 : 6));
//        buffer.putInt(dest.getPort());
//        buffer.put(dest.getAddress().getAddress());
//        peer.connection.send(buffer);
//    }
//
//    @Override
//    public void transferFrom(Peer peer) throws IOException{
//        ByteBuffer buffer = ByteBuffer.allocate(Byte.BYTES * 2 + Integer.BYTES);
//        peer.connection.recv(buffer);
//
//        isUDP = buffer.get() == 1;
//        ByteBuffer peerAddress;
//        switch(buffer.get()){
//            case 4:
//                peerAddress = ByteBuffer.allocate(4);
//                break;
//            case 6:
//                peerAddress = ByteBuffer.allocate(16);
//                break;
//            default: throw new IllegalArgumentException("Neither a IPv4 address nor a IPv6 address was sent");
//        }
//
//        peer.connection.recv(peerAddress);
//        dest = new InetSocketAddress(InetAddress.getByAddress(peerAddress.array()), buffer.getInt());
//        origin = new SoftReference<>(peer);
//    }

    @Override
    public int getSize(){
        return Byte.BYTES + Integer.BYTES + (dest.getAddress() instanceof Inet4Address ? 4 : 16);
    }

}

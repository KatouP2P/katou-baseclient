package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.network.TypeReadable;
import ga.nurupeaches.katou.network.TypeWritable;

import java.io.Flushable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class PeerConnection implements TypeWritable, TypeReadable, Flushable {

    private AsynchronousSocketChannel channel;
    private SocketAddress address;
    private Peer peer;

    /*
     * TODO: Handle UDP.
     */
    public PeerConnection(AsynchronousSocketChannel channel, Peer peer){
        this.channel = channel;
        this.peer = peer;

        try {
            address = channel.getRemoteAddress();
        } catch (IOException e){
            e.printStackTrace(); // TODO: handle
        }
    }

    public AsynchronousSocketChannel getChannel(){
        return channel;
    }

    public SocketAddress getAddress(){
        return address;
    }

    public void disconnect(){
        try {
            channel.close();
        } catch (IOException e){}

        channel = null;
        address = null;
    }

    @Override
    public void flush() throws IOException{
        channel.write(peer.buffer);
        peer.buffer.clear();
    }

    @Override
    public void writeString(String string){
        byte[] bytes = string.getBytes(Configuration.getCharset());
        writeInt(bytes.length);
        peer.buffer.put(bytes);
    }

    @Override
    public void writeLong(long l){
        peer.buffer.putLong(l);
    }

    @Override
    public void writeInt(int i){
        peer.buffer.putInt(i);
    }

    @Override
    public void writeShort(short s){
        peer.buffer.putShort(s);
    }

    @Override
    public void writeByte(byte b){
        peer.buffer.put(b);
    }

    @Override
    public void writeBytes(byte[] src){
        peer.buffer.put(src);
    }

    @Override
    public void writeBytes(ByteBuffer bb, int len){
        TypeWritable.writeBBtoBB(bb, peer.buffer, len);
    }

    @Override
    public String readString(){
        byte[] bytes = new byte[readInt()];
        readBytes(bytes);
        return new String(bytes, Configuration.getCharset());
    }

    @Override
    public long readLong(){
        return peer.buffer.getLong();
    }

    @Override
    public int readInt(){
        return peer.buffer.getInt();
    }

    @Override
    public short readShort(){
        return peer.buffer.getShort();
    }

    @Override
    public byte readByte(){
        return peer.buffer.get();
    }

    @Override
    public void readBytes(byte[] into){
        peer.buffer.get(into);
    }

    @Override
    public void readBytes(ByteBuffer bb, int len){
        TypeReadable.readBBtoBB(bb, peer.buffer, len);
    }

}

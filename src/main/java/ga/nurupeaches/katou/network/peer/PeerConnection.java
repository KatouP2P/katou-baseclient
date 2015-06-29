package ga.nurupeaches.katou.network.peer;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.network.TypeReadable;
import ga.nurupeaches.katou.network.TypeWritable;

import java.io.Flushable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;

public class PeerConnection<T extends Channel> implements TypeWritable, TypeReadable, Flushable {

    private Channel channel;
    private SocketAddress address;
    private Peer peer;

    /*
     * TODO: Handle UDP.
     */
    public PeerConnection(Channel channel, SocketAddress address, Peer peer){
        this.channel = channel;
        this.peer = peer;
        this.address = address;
    }

    public Channel getChannel(){
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
        if(channel instanceof AsynchronousSocketChannel){
            ((AsynchronousSocketChannel)channel).write(peer.outBuffer);
        } else if(channel instanceof DatagramChannel){
            ((DatagramChannel)channel).send(peer.outBuffer, address);
        }
        peer.outBuffer.clear();
    }

    @Override
    public void writeString(String string){
        byte[] bytes = string.getBytes(Configuration.getCharset());
        writeInt(bytes.length);
        peer.outBuffer.put(bytes);
    }

    @Override
    public void writeLong(long l){
        peer.outBuffer.putLong(l);
    }

    @Override
    public void writeInt(int i){
        peer.outBuffer.putInt(i);
    }

    @Override
    public void writeShort(short s){
        peer.outBuffer.putShort(s);
    }

    @Override
    public void writeByte(byte b){
        peer.outBuffer.put(b);
    }

    @Override
    public void writeBytes(byte[] src){
        peer.outBuffer.put(src);
    }

    @Override
    public void writeBytes(ByteBuffer bb, int len){
        TypeWritable.writeBBtoBB(bb, peer.outBuffer, len);
    }

    @Override
    public String readString(){
        byte[] bytes = new byte[readInt()];
        readBytes(bytes);
        return new String(bytes, Configuration.getCharset());
    }

    @Override
    public long readLong(){
        return peer.inBuffer.getLong();
    }

    @Override
    public int readInt(){
        return peer.inBuffer.getInt();
    }

    @Override
    public short readShort(){
        return peer.inBuffer.getShort();
    }

    @Override
    public byte readByte(){
        return peer.inBuffer.get();
    }

    @Override
    public void readBytes(byte[] into){
        peer.inBuffer.get(into);
    }

    @Override
    public void readBytes(ByteBuffer bb, int len){
        TypeReadable.readBBtoBB(bb, peer.inBuffer, len);
    }

}

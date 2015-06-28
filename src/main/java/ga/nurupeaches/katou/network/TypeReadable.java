package ga.nurupeaches.katou.network;

import java.nio.ByteBuffer;

public interface TypeReadable {

    public String readString();

    public long readLong();

    public int readInt();

    public short readShort();

    public byte readByte();

    public void readBytes(byte... arr);

    public void readBytes(ByteBuffer bb, int len);

    public static void readBBtoBB(ByteBuffer from, ByteBuffer to, int length){
        for(int i=0; i < length; i++){
            to.put(from.get());
        }
    }

}

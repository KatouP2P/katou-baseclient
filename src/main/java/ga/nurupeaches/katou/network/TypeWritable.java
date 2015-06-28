package ga.nurupeaches.katou.network;

import java.nio.ByteBuffer;

public interface TypeWritable {

    public void writeString(String string);

    public void writeLong(long l);

    public void writeInt(int i);

    public void writeShort(short s);

    public void writeByte(byte b);

    public void writeBytes(byte... src);

    public void writeBytes(ByteBuffer bb, int len);

    public static void writeBBtoBB(ByteBuffer from, ByteBuffer to, int length){
        for(int i=0; i < length; i++){
            to.put(from.get());
        }
    }

}

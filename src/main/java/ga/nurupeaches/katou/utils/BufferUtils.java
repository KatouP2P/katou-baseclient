package ga.nurupeaches.katou.utils;

import java.nio.ByteBuffer;

public final class BufferUtils {

    private BufferUtils(){}

    public static void copyCharsToBuffer(char[] arr, ByteBuffer buffer){
        for(char c : arr){
            buffer.putChar(c);
        }
    }

    public static void readBufferToChars(char[] arr, ByteBuffer buffer, int len){
        for(int i=0; i < len; i++){
            arr[i] = buffer.getChar();
        }
    }

    public static void writeString(String str, ByteBuffer buffer){
        char[] chars = str.toCharArray();
        buffer.putInt(chars.length);
        copyCharsToBuffer(chars, buffer);
    }

}

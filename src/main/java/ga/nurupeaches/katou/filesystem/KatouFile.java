package ga.nurupeaches.katou.filesystem;

import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.utils.BufferUtils;
import ga.nurupeaches.katou.utils.HashUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/*
 * Network Format:
 * ------------------------------------------
 * | Name Length | Name | Size | Hash bytes |
 * ------------------------------------------
 */
public class KatouFile implements Transmittable {

    public static final int HASH_SIZE = 32;
    private char[] name;
    private long size;
    private byte[] hash;

    public KatouFile(String str, long size, byte[] hash){
        this(str.toCharArray(), size, hash);
    }

    public KatouFile(char[] name, long size, byte[] hash){
        this.name = name;
        this.size = size;
        this.hash = hash;
    }

    public KatouFile(){}

    public static KatouFile fromFile(File file){
        KatouFile katouFile = new KatouFile();
        katouFile.name = file.getName().toCharArray();
        katouFile.size = file.length();
        katouFile.hash = HashUtils.computeHash(file);
        return katouFile;
    }

    public char[] getName(){
        return name;
    }

    public long getFileSize(){
        return size;
    }

    public byte[] getHash(){
        return hash;
    }

    @Override
    public void transferTo(Peer peer) throws IOException{
        if(peer == null){
            throw new IOException("peer cannot be null!");
        }

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + (name.length * Character.BYTES) + Long.BYTES + HASH_SIZE);
        // put the name of the file
        buffer.putInt(name.length);

        // write out the chars for the name;
        BufferUtils.copyCharsToBuffer(name, buffer);

        // put size
        buffer.putLong(size);
        // put the hash of the file
        buffer.put(hash);
        peer.connection.send(buffer);
    }

    @Override
    public void transferFrom(Peer peer) throws IOException {
        if(peer == null){
            throw new IOException("peer cannot be null!");
        }

        // name first
        int nameLen = peer.IN_BUFFER.getInt();
        name = new char[nameLen];
        BufferUtils.readBufferToChars(name, peer.IN_BUFFER, nameLen);

        // then size
        size = peer.IN_BUFFER.getLong();

        // and then the hash
        hash = new byte[HASH_SIZE];
        peer.IN_BUFFER.get(hash);
    }

    @Override
    public long getSize(){
        return Integer.BYTES + name.length * Character.BYTES + Long.BYTES + HASH_SIZE;
    }

    @Override
    public String toString(){
        return "KatouFile{name=" + new String(name) + ",size=" + size + ",hash=" + Arrays.toString(hash) + '}';
    }
}
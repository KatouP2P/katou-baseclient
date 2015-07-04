package ga.nurupeaches.katou.filesystem;

import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.utils.HashUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/*
 * Network Format:
 * ------------------------------------------
 * | Name Length | Name | Size | Hash bytes |
 * ------------------------------------------
 */
public class KatouFile implements Transmittable {

    public static final int HASH_SIZE = 32;
    private String name;
    private long size;
    private byte[] hash;

    public KatouFile(String name, long size, byte[] hash){
        this.name = name;
        this.size = size;
        this.hash = hash;
    }

    public KatouFile(){}

    public static KatouFile fromFile(File file){
        KatouFile katouFile = new KatouFile();
        katouFile.name = file.getName();
        katouFile.size = file.length();
        katouFile.hash = HashUtils.computeHash(file);
        return katouFile;
    }

    public String getName(){
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

        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + nameBytes.length + Long.BYTES + HASH_SIZE);
        // put the name of the file
        buffer.putInt(nameBytes.length);
        buffer.put(nameBytes);
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

        // ugh, have to read the name length first because of how poorly i designed this :^)
        ByteBuffer nameLenBuffer = ByteBuffer.allocate(Integer.BYTES);
        peer.connection.recv(nameLenBuffer);
        int nameLen = nameLenBuffer.getInt();
        // now let's buffer the rest of the information in.
        ByteBuffer buffer = ByteBuffer.allocate(nameLen + Long.BYTES + HASH_SIZE);
        peer.connection.recv(buffer);
        // and then parse it out into our variables...
        // name first
        byte[] nameBytes = new byte[nameLen];
        buffer.get(nameBytes);
        name = new String(nameBytes, StandardCharsets.UTF_8);
        // then size
        size = buffer.getLong();
        // and then the hash
        hash = new byte[HASH_SIZE];
        buffer.get(hash);
    }

}
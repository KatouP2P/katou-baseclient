package ga.nurupeaches.katou.filesystem;

import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.utils.HashUtils;

import java.io.File;
import java.io.IOException;

/*
      * Network Format:
      * ------------------------------------------
      * | Name Length | Name | Size | Hash bytes |
      * ------------------------------------------
     */
public class KatouFile implements Transmittable {

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

    public long getSize(){
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

        peer.connection.writeString(name);
        peer.connection.writeLong(size);
        peer.connection.writeBytes(hash);
    }

    @Override
    public void transferFrom(Peer peer) throws IOException {
        if(peer == null){
            throw new IOException("peer cannot be null!");
        }

        name = peer.connection.readString();
        size = peer.connection.readLong();
        hash = new byte[32];
        peer.connection.readBytes(hash);
    }

}
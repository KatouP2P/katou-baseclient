package ga.nurupeaches.katou.filesystem;

import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.utils.BufferUtils;
import ga.nurupeaches.katou.utils.HashUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/*
 * Network Format:
 * ---------------------------------------------------------------------=========================
 * | Name Length | Name | Size | Hash bytes | Bit if in Directory (0/1) | Directory name/subdir |
 * ---------------------------------------------------------------------=========================
 */
public class KatouFile implements Transmittable, Parentable<KatouDirectory>, Nameable {

    public static final int HASH_SIZE = 32;
    private KatouDirectory parent;
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

    public void setFileSize(long size){
        this.size = size;
    }

    public long getFileSize(){
        return size;
    }

    public void setHash(byte[] hash){
        this.hash = hash;
    }

    public byte[] getHash(){
        return hash;
    }

    @Override
    public void setName(char[] name){
        this.name = name;
    }

    @Override
    public char[] getName(){
        return name;
    }

    @Override
    public KatouDirectory getParent(){
        return parent;
    }

    @Override
    public void setParent(KatouDirectory parent){
        this.parent = parent;
    }

    @Override
    public void transferTo(Peer peer) throws IOException{
        if(peer == null){
            throw new IOException("peer cannot be null!");
        }

        ByteBuffer buffer = ByteBuffer.allocate(getSize());

        // write id
        buffer.put((byte) 0x02);
        // write memory size
        buffer.putInt(getSize());
        // put the length of the name of the file
        buffer.putInt(name.length);
        // write out the chars for the name
        BufferUtils.copyCharsToBuffer(name, buffer);
        // put size
        buffer.putLong(size);
        // put the hash of the file
        buffer.put(hash);

        if(parent != null){
            buffer.put((byte) 1);
            // put the length of the name of the parent
            buffer.putInt(parent.getName().length);
            // write out the chars for the parent
            BufferUtils.copyCharsToBuffer(parent.getName(), buffer);
        } else {
            buffer.put((byte)0);
        }

        peer.connection.send(buffer);
    }

    @Override
    public void transferFrom(Peer peer) throws IOException {
        if(peer == null){
            throw new IOException("peer cannot be null!");
        }

        ByteBuffer nameLen = ByteBuffer.allocate(Integer.BYTES);
        ByteBuffer size = ByteBuffer.allocate(Long.BYTES);
        ByteBuffer hash = ByteBuffer.allocate(HASH_SIZE);
        ByteBuffer hasParent = ByteBuffer.allocate(1);

        ByteBuffer[] buffers = new ByteBuffer[]{nameLen, size, hash, hasParent};
        peer.connection.recv();

        // name first
        int nameLen = peer.IN_BUFFER.getInt();
        name = new char[nameLen];
        BufferUtils.readBufferToChars(name, peer.IN_BUFFER, nameLen);

        // then size
        size = peer.IN_BUFFER.getLong();

        // and then the hash
        hash = new byte[HASH_SIZE];
        peer.IN_BUFFER.get(hash);

        byte hasParent = peer.IN_BUFFER.get();
        switch(hasParent){
            case 0:
                break;
            case 1:
                int parentNameLen = peer.IN_BUFFER.getInt();
                char[] parentName = new char[parentNameLen];
                BufferUtils.readBufferToChars(parentName, peer.IN_BUFFER, parentNameLen);

        }
    }

    @Override
    public int getSize(){
        return Byte.BYTES + Integer.BYTES * 2 + (name.length * Character.BYTES)
                + Long.BYTES + HASH_SIZE + Byte.BYTES +
                (parent == null ? 0 : Integer.BYTES + parent.getName().length * Character.BYTES);
    }

    @Override
    public String toString(){
        return "KatouFile{name=" + new String(name) + ",size=" + size + ",hash=" + HashUtils.hexifyArray(hash) + '}';
    }
}
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

        ByteBuffer nameLenBuffer = ByteBuffer.allocate(Integer.BYTES);
        ByteBuffer sizeBuffer = ByteBuffer.allocate(Long.BYTES);
        ByteBuffer hashBuffer = ByteBuffer.allocate(HASH_SIZE);
        ByteBuffer parentNameLengthBuffer = ByteBuffer.allocate(Integer.BYTES);

        peer.connection.recv(new ByteBuffer[]{nameLenBuffer, sizeBuffer, hashBuffer, parentNameLengthBuffer});

        // name first
        name = new char[nameLenBuffer.getInt()];
        if(name.length <= 0) throw new IllegalArgumentException("Invalid KatouFile name");

        // then size
        size = sizeBuffer.getLong();

        // and then the hash
        hash = new byte[HASH_SIZE];
        hashBuffer.get(hash);

        int parentNameLength = parentNameLengthBuffer.getInt();
        if(parentNameLength != 0){
            ByteBuffer parentNameBuffer = ByteBuffer.allocate(parentNameLength * Character.BYTES);
            peer.connection.recv(parentNameBuffer);

            char[] parentName = new char[parentNameLength];
            BufferUtils.readBufferToChars(name, parentNameBuffer, parentName.length);
        }

        ByteBuffer nameBuffer = ByteBuffer.allocate(name.length * Character.BYTES);
        peer.connection.recv(nameBuffer);
        BufferUtils.readBufferToChars(name, nameBuffer, name.length);
    }

    @Override
    public int getSize(){
        return Integer.BYTES + name.length * Character.BYTES
                + Long.BYTES + HASH_SIZE + Byte.BYTES +
                (parent == null ? 0 : Integer.BYTES + parent.getName().length * Character.BYTES);
    }

    @Override
    public String toString(){
        return "KatouFile{name=" + new String(name) + ",size=" + size + ",hash=" + HashUtils.hexifyArray(hash) + '}';
    }
}
package ga.nurupeaches.katou.filesystem;

import ga.nurupeaches.common.utils.HashUtils;
import ga.nurupeaches.serichan.Transmittable;

import java.io.File;

/*
 * Network Format:
 * --------------------------------------------------------------===============
 * | Name Length | Size | Hash bytes | Parent Name Length | Name | Parent Name |
 * --------------------------------------------------------------===============
 */
public class KatouFile implements Transmittable, Parentable<KatouDirectory>, Nameable {

    public static final int HASH_SIZE = 32;
    private KatouDirectory parent;
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
    public void setName(String name){
        this.name = name;
    }

    @Override
    public String getName(){
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

//    @Override
//    public void transferTo(Peer peer) throws IOException{
//        if(peer == null){
//            throw new IOException("peer cannot be null!");
//        }
//
//        ByteBuffer buffer = ByteBuffer.allocate(getSize());
//
//        // put the length of the name of the file
//        buffer.putInt(name.length);
//        // put size
//        buffer.putLong(size);
//        // put the hash of the file
//        buffer.put(hash);
//
//        // write out the chars for the name
//        BufferUtils.copyCharsToBuffer(name, buffer);
//
//        if(parent != null){
//            buffer.putInt(parent.getName().length);
//            // write out the chars for the parent
//            BufferUtils.copyCharsToBuffer(parent.getName(), buffer);
//        } else {
//            buffer.putInt(0);
//        }
//
//        peer.connection.send(buffer);
//    }

//    @Override
//    public void transferFrom(Peer peer) throws IOException {
//        if(peer == null){
//            throw new IOException("peer cannot be null!");
//        }
//
//        ByteBuffer nameLenBuffer = ByteBuffer.allocate(Integer.BYTES);
//        ByteBuffer sizeBuffer = ByteBuffer.allocate(Long.BYTES);
//        ByteBuffer hashBuffer = ByteBuffer.allocate(HASH_SIZE);
//        ByteBuffer parentNameLengthBuffer = ByteBuffer.allocate(Integer.BYTES);
//
//        peer.connection.recv(new ByteBuffer[]{nameLenBuffer, sizeBuffer, hashBuffer, parentNameLengthBuffer});
//
//        // name first
//        name = new char[nameLenBuffer.getInt()];
//        if(name.length <= 0) throw new IllegalArgumentException("Invalid KatouFile name");
//
//        // then size
//        size = sizeBuffer.getLong();
//
//        // and then the hash
//        hash = new byte[HASH_SIZE];
//        hashBuffer.get(hash);
//
//        int parentNameLength = parentNameLengthBuffer.getInt();
//        if(parentNameLength != 0){
//            ByteBuffer parentNameBuffer = ByteBuffer.allocate(parentNameLength * Character.BYTES);
//            peer.connection.recv(parentNameBuffer);
//
//            char[] parentName = new char[parentNameLength];
//            BufferUtils.readBufferToChars(parentName, parentNameBuffer, parentName.length);
//            // TODO: find KatouDirectory or make a new one based on parentName
//        }
//
//        ByteBuffer nameBuffer = ByteBuffer.allocate(name.length * Character.BYTES);
//        peer.connection.recv(nameBuffer);
//        BufferUtils.readBufferToChars(name, nameBuffer, name.length);
//    }

    @Override
    public int getSize(){
        return Integer.BYTES + Long.BYTES + HASH_SIZE + Integer.BYTES + name.length() * Character.BYTES +
                (parent == null ? 0 : parent.getName().length() * Character.BYTES);
    }

    @Override
    public String toString(){
        return "KatouFile{name=" + name + ",size=" + size + ",hash=" + HashUtils.hexifyArray(hash) + '}';
    }
}
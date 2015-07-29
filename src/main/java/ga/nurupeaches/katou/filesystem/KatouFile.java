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

    public KatouFile(File file){
        this(file.getName(), file.length(), HashUtils.computeHash(file));
    }

    public KatouFile(){}

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
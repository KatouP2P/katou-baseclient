package ga.nurupeaches.katou.filesystem;

import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.utils.BufferUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Network Format:
 * ----------------------------------------------------------
 * | Name Length | Name | Amount of KatouFiles | KatouFiles |
 * ----------------------------------------------------------
 */
public class KatouDirectory implements Transmittable {

    // Map<FileName, Information>
    private Map<char[], KatouFile> files;

    // TODO: Handle transfering of sub-directories.
    private Map<char[], KatouDirectory> subdirectories;
    private char[] directoryName;

    public KatouDirectory(){}

    public KatouDirectory(String dir){
        this(new File(dir));
    }

    public KatouDirectory(File dir){
        if(dir == null){
            throw new IllegalArgumentException("dir cannot be null!");
        }

        if(!dir.isDirectory()){
            throw new IllegalArgumentException("dir cannot be anything other than a directory!");
        }

        directoryName = dir.getName().toCharArray();
        populateMap(dir);
    }

    public void populateMap(File file){
        if(file == null){
            throw new NullPointerException();
        }

        File[] ls = file.listFiles();
        if(ls == null) return;

        for(File subfile : ls){
            if(subfile.isDirectory()){
                subdirectories.put(subfile.getName().toCharArray(), new KatouDirectory(subfile));
            }

            if(files == null) files = new ConcurrentHashMap<>(1);
            files.put(subfile.getName().toCharArray(), KatouFile.fromFile(subfile));
        }
    }

    // TODO: verify transferTo() and transferFrom() correlate. also, support subdirectories.

    @Override
    public void transferTo(Peer peer) throws IOException {
        if(peer == null){
            throw new IOException("peer cannot be null!");
        }

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + (directoryName.length * Character.BYTES) + Integer.BYTES);

        // write directory name
        buffer.putInt(directoryName.length);
        BufferUtils.copyCharsToBuffer(directoryName, buffer);

        // write the amount of files
        buffer.putInt(files.size());

        // ship out the first package of data
        peer.connection.send(buffer);

        // now transfer the rest out
        for(KatouFile info : files.values()){
            info.transferTo(peer);
        }
    }

    @Override
    public void transferFrom(Peer peer) throws IOException {
        if(peer == null){
            throw new IOException("peer cannot be null!");
        }

        int directoryNameLen = peer.IN_BUFFER.getInt();
        directoryName = new char[directoryNameLen];

        BufferUtils.readBufferToChars(directoryName, peer.IN_BUFFER, directoryNameLen);

        int amount = peer.IN_BUFFER.getInt();
        KatouFile info;
        for(int i=0; i < amount; i++){
            info = new KatouFile();
            info.transferFrom(peer);
            files.put(info.getName(), info);
        }
    }

    @Override
    public long getSize() throws IOException{
        long size = Integer.BYTES + (directoryName.length * Character.BYTES) + Integer.BYTES;
        for(KatouFile file : files.values()){
            size += file.getSize();
        }
        return size;
    }
}

package ga.nurupeaches.katou.filesystem;

import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Network Format:
 * ----------------------------------------------------------
 * | Name Length | Name | Amount of KatouFiles | KatouFiles |
 * ----------------------------------------------------------
 */
public class KatouDirectory implements Transmittable, Serializable {

    // Map<FileName, Information>
    private Map<String, KatouFile> files;

    // TODO: Handle transfering of sub-directories.
    private Map<String, KatouDirectory> subdirectories;
    private String directoryName;

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

        directoryName = dir.getName();
        populateMap(dir);
    }

    public void populateMap(File file){
        for(File subfile : file.listFiles()){
            if(subfile.isDirectory()){
                subdirectories.put(subfile.getName(), new KatouDirectory(subfile));
            }

            if(files == null) files = new ConcurrentHashMap<>(1);
            files.put(subfile.getName(), KatouFile.fromFile(subfile));
        }
    }

    // TODO: verify transferTo() and transferFrom() correlate. also, support subdirectories.

    @Override
    public void transferTo(Peer peer) throws IOException {
        if(peer == null){
            throw new IOException("peer cannot be null!");
        }

        byte[] directoryNameBytes = directoryName.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + directoryNameBytes.length + Integer.BYTES);
        // write directory name
        buffer.putInt(directoryNameBytes.length);
        buffer.put(directoryNameBytes);
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

        ByteBuffer directoryNameLength = ByteBuffer.allocate(Integer.BYTES);
        peer.connection.recv(directoryNameLength);
        int directoryNameLen = directoryNameLength.getInt();

        ByteBuffer buffer = ByteBuffer.allocate(directoryNameLen + Integer.BYTES);
        peer.connection.recv(buffer);

        byte[] directoryNameBytes = new byte[directoryNameLen];
        buffer.get(directoryNameBytes);
        directoryName = new String(directoryNameBytes, StandardCharsets.UTF_8);

        int amount = buffer.getInt();
        KatouFile info;
        for(int i=0; i < amount; i++){
            info = new KatouFile();
            info.transferFrom(peer);
            files.put(info.getName(), info);
        }
    }

}

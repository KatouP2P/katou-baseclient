package ga.nurupeaches.katou.filesystem;

import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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

    @Override
    public void transferTo(Peer peer) throws IOException {
        if(peer == null){
            throw new IOException("peer cannot be null!");
        }

        peer.connection.writeString(directoryName);
        peer.connection.writeInt(files.size());
        for(KatouFile info : files.values()){
            info.transferTo(peer);
        }
    }

    @Override
    public void transferFrom(Peer peer) throws IOException {
        if(peer == null){
            throw new IOException("peer cannot be null!");
        }

        directoryName = peer.connection.readString();
        int amount = peer.connection.readInt();
        KatouFile info;
        for(int i=0; i < amount; i++){
            info = new KatouFile();
            info.transferFrom(peer);
            files.put(info.getName(), info);
        }
    }

}

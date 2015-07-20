package ga.nurupeaches.katou.filesystem;

import ga.nurupeaches.serichan.Transmittable;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Network Format:
 * ---------------------------------------------------------------------------------------------==============================================
 * | Name Length | Amount of KatouFiles | Amount of KatouDirectorys | Parent Name Length | Name | Parent Name | KatouFiles | KatouDirectorys |
 * ---------------------------------------------------------------------------------------------==============================================
 */
public class KatouDirectory implements Transmittable, Parentable<KatouDirectory>, Nameable {

    // Map<FileName, Information>
    private KatouDirectory parent;
    private Map<String, KatouFile> files;
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
        if(file == null){
            throw new NullPointerException();
        }

        File[] ls = file.listFiles();
        if(ls == null) return;

        KatouDirectory subdir;
        KatouFile fileInDir;
        for(File subfile : ls){
            if(subfile.isDirectory()){
                if(subdirectories == null){
                    subdirectories = new ConcurrentHashMap<>(1);
                }

                subdir = new KatouDirectory(subfile);
                subdir.setParent(this);
                subdirectories.put(subdir.getName(), subdir);
            } else {
                if(files == null) files = new ConcurrentHashMap<>(1);

                fileInDir = KatouFile.fromFile(subfile);
                fileInDir.setParent(this);
                files.put(fileInDir.getName(), fileInDir);
            }
        }
    }

    public Collection<KatouFile> getFiles(){ return files.values(); }

    public Collection<KatouDirectory> getSubdirectories(){ return subdirectories.values(); }

    @Override
    public void setName(String name){
        this.directoryName = name;
    }

    @Override
    public String getName(){
        return directoryName;
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
//    public void transferTo(Peer peer) throws IOException {
//        if(peer == null){
//            throw new IOException("peer cannot be null!");
//        }
//
//        ByteBuffer buffer = ByteBuffer.allocate(getSize());
//
//        // write directory name length
//        buffer.putInt(directoryName.length);
//
//        if(files != null){
//            // write the amount of files
//            buffer.putInt(files.size());
//        } else {
//            buffer.putInt(0);
//        }
//
//        if(subdirectories != null){
//            // write the amount of subdirectories
//            buffer.putInt(subdirectories.size());
//        } else {
//            buffer.putInt(0);
//        }
//
//        // write length of directory name
//        if(parent != null){
//            buffer.putInt(parent.directoryName.length);
//        } else {
//            buffer.putInt(0);
//        }
//
//        // write name of file
//        BufferUtils.copyCharsToBuffer(directoryName, buffer);
//
//        if(parent != null){
//            BufferUtils.copyCharsToBuffer(parent.directoryName, buffer);
//        }
//
//        for(KatouFile file : files.values()){
//            file.transferTo(peer);
//        }
//
//        for(KatouDirectory subdir : subdirectories.values()){
//            subdir.transferTo(peer);
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
//        int directoryNameLen = peer.IN_BUFFER.getInt();
//        directoryName = new char[directoryNameLen];
//
//        BufferUtils.readBufferToChars(directoryName, peer.IN_BUFFER, directoryNameLen);
//
//        int fileCount = peer.IN_BUFFER.getInt();
//        if(fileCount != 0){
//            files = new ConcurrentHashMap<>(fileCount);
//        }
//
//        int subdirectoryCount = peer.IN_BUFFER.getInt();
//        if(subdirectoryCount != 0){
//            subdirectories = new ConcurrentHashMap<>(subdirectoryCount);
//        }
//
//        byte hasParent = peer.IN_BUFFER.get();
//        switch(hasParent){
//            case 0:
//                break;
//            case 1:
//                int parentNameLen = peer.IN_BUFFER.getInt();
//                char[] parentName = new char[parentNameLen];
//                BufferUtils.readBufferToChars(parentName, peer.IN_BUFFER, parentNameLen);
//
//        }
//    }

    @Override
    public int getSize(){
        return Integer.BYTES + directoryName.length() * Character.BYTES
                + Integer.BYTES + Integer.BYTES + Byte.BYTES +
                (parent == null ? 0 : Integer.BYTES + parent.getName().length() * Character.BYTES);
    }

    @Override
    public String toString(){
        return "KatouDirectory{filesCount=" + (files != null ? files.size() : 0) + ",subdirectoryCount=" +
                (subdirectories != null ? subdirectories.size() : 0) + ",name=" + directoryName + '}';
    }

}

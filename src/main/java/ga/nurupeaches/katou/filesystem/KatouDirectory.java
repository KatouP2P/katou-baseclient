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

                fileInDir = new KatouFile(subfile);
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

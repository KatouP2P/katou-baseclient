package ga.nurupeaches.katou.chunk;

public abstract class RepresentableChunk implements Chunk {

    /**
     * Size of this chunk.
     */
    private int chunkSize;

    /**
     * ID of this chunk.
     */
    private int id;

    public RepresentableChunk(int id, int size){
        this.id = id;
        this.chunkSize = size;
    }

    public RepresentableChunk(){}

    @Override
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    @Override
    public int getSize(){
        return chunkSize;
    }

    public void setSize(int id){
        chunkSize = id;
    }
}

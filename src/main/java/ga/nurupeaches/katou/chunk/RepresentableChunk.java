package ga.nurupeaches.katou.chunk;

/**
 * A chunk that has an ID and size.
 */
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

    @Override
    public int getSize(){
        return chunkSize + Integer.BYTES * 2;
    }

}

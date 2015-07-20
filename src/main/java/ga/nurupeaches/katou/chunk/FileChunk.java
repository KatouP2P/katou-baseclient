package ga.nurupeaches.katou.chunk;

import ga.nurupeaches.katou.KatouClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.zip.CRC32;

/**
 * Represents a chunk of a file on disk.
 */
public class FileChunk extends RepresentableChunk {

    /**
     * The file's channel.
     */
    private AsynchronousFileChannel fileChannel;

    /**
     * The index in which we are reading/writing from.
     */
    private int index;

    /**
     * Constructs a FileChunk with the given size.
     * @param id The ID of this chunk.
     * @param size Size of chunk in KB (kilobytes)
     * @param index The index in which we are reading/writing from.
     */
    public FileChunk(AsynchronousFileChannel fileChannel, int id, int size, int index){
        super(id, size);
        this.index = index;
        this.fileChannel = fileChannel;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }

//    @Override
//    public void transferFrom(Peer peer) throws IOException{
//        // try to obtain a lock
//        FileLock lock;
//        try {
//            lock = fileChannel.tryLock();
//        } catch (OverlappingFileLockException e){
//            Server.NETWORK_LOGGER.log(Level.SEVERE, "Attempt to grab lock on file failed; there was an overlapping lock!", e);
//            return;
//        }
//        if(lock == null){
//            return;
//        }
//
//        // read basic information about the chunk
//        setId(peer.IN_BUFFER.getInt());
//        setSize(peer.IN_BUFFER.getInt());
//        setIndex(peer.IN_BUFFER.getInt());
//
//        // store the old limit
//        int oldLim = peer.IN_BUFFER.limit();
//        // limit the buffer to the region
//        peer.IN_BUFFER.limit(peer.IN_BUFFER.position() + getSize());
//
//        // write buffer from pos to lim to the file
//        lock.channel().write(peer.IN_BUFFER, index);
//
//        // reset the limit
//        peer.IN_BUFFER.limit(oldLim);
//
//        // release lock
//        lock.release();
//    }
//
//    @Override
//    public void transferTo(Peer peer) throws IOException{
//        // allocate the space required for this chunk
//        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 3 + getSize());
//        buffer.putInt(getId());
//        buffer.putInt(getSize());
//        buffer.putInt(index);
//
//        // try to obtain a lock
//        FileLock lock;
//        try {
//            lock = fileChannel.tryLock();
//        } catch (OverlappingFileLockException e){
//            Server.NETWORK_LOGGER.log(Level.SEVERE, "Attempt to grab lock on file failed; there was an overlapping lock!", e);
//            return;
//        }
//        if(lock == null) return;
//
//        // read file into buffer
//        lock.channel().read(buffer);
//
//        // release lock
//        lock.release();
//
//        // write data to peer
//        peer.connection.send(buffer);
//    }

    @Override
    public boolean validate(long crc32){
        ByteBuffer buffer = ByteBuffer.allocate(getSize());

        try {
            FileLock lock = fileChannel.tryLock(index, getSize(), false);
            lock.channel().read(buffer);
            lock.release();
        } catch (IOException e){
            KatouClient.LOGGER.log(Level.WARNING, "Failed to obtain lock on async FileChunk " + toString(), e);
        }

        CRC32 crc = new CRC32();
        crc.update(buffer);
        return crc.getValue() == crc32;
    }

}

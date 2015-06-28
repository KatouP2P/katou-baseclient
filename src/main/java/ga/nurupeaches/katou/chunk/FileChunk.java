package ga.nurupeaches.katou.chunk;

import ga.nurupeaches.katou.KatouClient;
import ga.nurupeaches.katou.network.peer.Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.zip.CRC32;

public class FileChunk extends RepresentableChunk {

    private AsynchronousFileChannel fileChannel;
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

    @Override
    public void transferFrom(Peer peer) throws IOException{

    }

    @Override
    public void transferTo(Peer peer) throws IOException{

    }

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

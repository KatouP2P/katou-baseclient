package ga.nurupeaches.katou.network.server.handlers;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.chunk.Chunk;
import ga.nurupeaches.katou.chunk.MemoryChunk;
import ga.nurupeaches.katou.filesystem.KatouDirectory;
import ga.nurupeaches.katou.filesystem.KatouFile;
import ga.nurupeaches.katou.filesystem.Nameable;
import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.server.Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DataHandler implements CompletionHandler<Integer, Peer> {

    private int readTries = 0;
    private final ByteBuffer buffer;

    public DataHandler(ByteBuffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void completed(Integer result, Peer peer){
        buffer.flip();
        buffer.limit(result);

        while(result > 0){
            if(readTries > 3){
                // IllegalStateException doesn't really "fit" here, but it's the closest.
                failed(new IllegalStateException("Too many attempts to read!"), peer);
                break;
            }

            result -= parseData(peer);
            readTries++;
        }

        readTries = 0;
        buffer.clear();
        ((AsynchronousSocketChannel)peer.connection.getRawChannel()).read(buffer, peer, this);
    }

    /**
     * @param peer Peer that we've recieved information from
     * @return The amount of bytes that should have been read from buffer (essentially the size of the Transmittable file
     */
    public int parseData(Peer peer){
        byte id = buffer.get();
        int size = buffer.getInt();

        if(size > peer.IN_BUFFER.capacity()){
            ByteBuffer ext = ByteBuffer.allocate(size);
            ext.put(peer.IN_BUFFER);
            ((AsynchronousSocketChannel)peer.connection.getRawChannel()).read(ext, Configuration.getTimeout(), TimeUnit.SECONDS, peer, null);
            peer.IN_BUFFER = ext;
        }

        Transmittable transmittable = null;
        switch(id){
            case 0x01:
                transmittable = new MemoryChunk();
                break;

            case 0x02:
                transmittable = new KatouFile();
                break;

            case 0x03:
                transmittable = new KatouDirectory();
                break;
        }

        if(transmittable != null){
            try {
                transmittable.transferFrom(peer);

                if(transmittable instanceof Nameable){
                    peer.OFFERED.put(((Nameable)transmittable).getName(), transmittable);
                } else if(transmittable instanceof Chunk){
                    // figure out what to do
                } else {
                    // u wot
                }

                System.out.println(transmittable.toString());
            } catch (IOException e){
                failed(e, peer);
            }
        }

        return Byte.BYTES + Integer.BYTES + size;
    }

    @Override
    public void failed(Throwable throwable, Peer peer){
        Server.NETWORK_LOGGER.log(Level.WARNING, "Encountered error while parsing information", throwable);
    }

}

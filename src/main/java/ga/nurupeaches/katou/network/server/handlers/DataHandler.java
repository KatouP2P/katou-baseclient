package ga.nurupeaches.katou.network.server.handlers;

import ga.nurupeaches.katou.chunk.MemoryChunk;
import ga.nurupeaches.katou.filesystem.KatouDirectory;
import ga.nurupeaches.katou.filesystem.KatouFile;
import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class DataHandler implements CompletionHandler<Integer, Peer> {

    private final ByteBuffer BUFFER;

    public DataHandler(ByteBuffer buffer){
        if(buffer.capacity() > 1){
            System.out.println("capacity() is a bit large for a single byte.");
        }

        BUFFER = buffer;
    }

    @Override
    public void completed(Integer result, Peer peer){
        BUFFER.flip();

        byte id = BUFFER.get();
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
            } catch (IOException e){
                failed(e, peer);
            }
        }
    }

    @Override
    public void failed(Throwable exc, Peer peer){
        exc.printStackTrace(); // TODO: Handle
    }

}

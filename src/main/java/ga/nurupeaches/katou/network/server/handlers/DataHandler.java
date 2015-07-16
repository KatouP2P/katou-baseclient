package ga.nurupeaches.katou.network.server.handlers;

import ga.nurupeaches.katou.chunk.Chunk;
import ga.nurupeaches.katou.chunk.MemoryChunk;
import ga.nurupeaches.katou.filesystem.KatouDirectory;
import ga.nurupeaches.katou.filesystem.KatouFile;
import ga.nurupeaches.katou.filesystem.Nameable;
import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;
import ga.nurupeaches.katou.network.server.Server;
import ga.nurupeaches.katou.proxy.ProxyRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;

public class DataHandler implements CompletionHandler<Integer, Peer> {

    private final ByteBuffer buffer;

    public DataHandler(ByteBuffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void completed(Integer result, Peer peer){
        buffer.flip();

        if(result == buffer.capacity()){
            parseData(peer);
        }

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

            case 0x11:
                transmittable = new ProxyRequest();
                break;

            case 0x12:
                // data to forward to server
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

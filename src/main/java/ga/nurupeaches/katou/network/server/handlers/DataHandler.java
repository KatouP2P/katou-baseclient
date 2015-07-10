package ga.nurupeaches.katou.network.server.handlers;

import ga.nurupeaches.katou.chunk.MemoryChunk;
import ga.nurupeaches.katou.filesystem.KatouDirectory;
import ga.nurupeaches.katou.filesystem.KatouFile;
import ga.nurupeaches.katou.network.Transmittable;
import ga.nurupeaches.katou.network.peer.Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class DataHandler implements CompletionHandler<Integer, Peer> {

    private final ByteBuffer buffer;

    public DataHandler(ByteBuffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void completed(Integer result, Peer peer){
        buffer.flip();
        parseData(peer);
        if(peer.IN_BUFFER.remaining())
        ((AsynchronousSocketChannel) peer.connection.getRawChannel()).read(buffer, peer, this);
    }

    public void parseData(Peer peer){
        byte id = buffer.get();
        long size = buffer.getLong();

        if(size > peer.IN_BUFFER.capacity()){
            ByteBuffer ext = ByteBuffer.allocate((int)(size - buffer.capacity()));
            ((AsynchronousSocketChannel)peer.connection.getRawChannel()).read(ext, 30, TimeUnit.SECONDS, peer, null);
            // todo: handle extension buffers
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
                System.out.println(transmittable.toString());
            } catch (IOException e){
                failed(e, peer);
            }
        }

        buffer.compact();
    }

    @Override
    public void failed(Throwable exc, Peer peer){
        exc.printStackTrace(); // TODO: Handle
    }

}

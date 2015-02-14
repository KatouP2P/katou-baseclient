package ga.nurupeaches.kato.io;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Interface for a chunk that can go from peer to peer.
 */
public interface TransferableChunk extends Chunk {

	/**
	 * Begins to transfer from a peer.
	 * @param channel - The byte channel of the peer.
	 * @throws IOException
	 */
	public void transferFromPeer(ReadableByteChannel channel) throws IOException;

	/**
	 * Begins to transfer to a peer.
	 * @param channel - Peer to transfer to.
	 * @throws IOException
	 */
	public void transferToPeer(WritableByteChannel channel) throws IOException;

}

package ga.nurupeaches.katou.io;

import ga.nurupeaches.katou.network.Peer;

import java.io.IOException;

/**
 * Interface for a chunk that can go from peer to peer.
 */
public interface TransferableChunk extends Chunk {

	/**
	 * Begins to transfer from a peer.
	 * @param peer - Peer to transfer from.
	 * @throws IOException
	 */
	public void transferFromPeer(Peer peer) throws IOException;

	/**
	 * Begins to transfer to a peer.
	 * @param peer - Peer to transfer to.
	 * @throws IOException
	 */
	public void transferToPeer(Peer peer) throws IOException;

}

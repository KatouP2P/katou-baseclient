package ga.nurupeaches.katou.network;

import ga.nurupeaches.katou.network.manager.SocketWrapper;
import ga.nurupeaches.katou.network.manager.NetworkManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a peer.
 */
public class Peer {

	private final ByteBuffer buffer = ByteBuffer.allocate(NetworkManager.DEFAULT_BUFFER_SIZE);
	private final SocketWrapper socket;
	private List<KatouFile> files = new ArrayList<KatouFile>();
	private String version;
	private int nextBlock;

	/**
	 * Constructs a peer with the given socket.
	 * @param socket The socket of the peer.
	 */
	public Peer(SocketWrapper socket){
		this.socket = socket;
	}

	public void registerFile(KatouFile file){
		files.add(file);
	}

	public boolean hasFile(KatouMetadata metadata){
		return files.contains(metadata);
	}

	public void setNextBlock(int nextBlock) {
		this.nextBlock = nextBlock;
	}

	public int getNextBlock() {
		return nextBlock;
	}

	public SocketWrapper getSocket(){
		return socket;
	}

	public String getVersion(){
		return version;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setVersion(String version){
		this.version = version;
	}

	@Override
	public boolean equals(Object other){
		return other instanceof Peer && ((Peer)other).socket.equals(socket);
	}

}
package ga.nurupeaches.kato.network;

import ga.nurupeaches.kato.network.manager.NetworkManager;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.NetworkChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a peer.
 */
public class Peer<T extends ByteChannel & NetworkChannel> {

	private final ByteBuffer buffer = ByteBuffer.allocate(NetworkManager.DEFAULT_BUFFER_SIZE);
	private final T channel;
	private List<KatouFile> files = new ArrayList<>();
	private String version;
	private int nextBlock;

	/**
	 * Constructs a peer with the given channel.
	 * @param channel The channel of the peer.
	 */
	public Peer(T channel){
		this.channel = channel;
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

	public T getChannel(){
		return channel;
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
		return other instanceof Peer && ((Peer)other).channel.equals(channel);
	}

}
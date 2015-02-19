package ga.nurupeaches.katou.network;

import ga.nurupeaches.katou.network.manager.ChannelWrapper;
import ga.nurupeaches.katou.network.manager.NetworkManager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a peer.
 */
public class Peer {

	private final ByteBuffer buffer = ByteBuffer.allocate(NetworkManager.DEFAULT_BUFFER_SIZE);
	private final ChannelWrapper channel;
	private List<KatouFile> files = new ArrayList<>();
	private String version;
	private int nextBlock;

	/**
	 * Constructs a peer with the given channel.
	 * @param channel The channel of the peer.
	 */
	public Peer(ChannelWrapper channel){
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

	public ChannelWrapper getChannel(){
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
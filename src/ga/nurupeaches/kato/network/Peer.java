package ga.nurupeaches.kato.network;

import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.NetworkChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a peer.
 */
public class Peer<T extends ByteChannel & NetworkChannel> {

	private final T channel;
	private List<KatouFile> files = new ArrayList<>();
	private String version;
	private int nextBlock;


	/**
	 * Constructs a peer with the given address.
	 * @param address - The address of the peer.
	 */
	public Peer(T channel){
		this.channel = channel;
	}

	@Override
	public boolean equals(Object other){
		return other instanceof Peer && ((Peer)other).channel.equals(channel);
	}

	public void registerFile(KatouFile file){
		files.add(file);
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

	public void setVersion(String version){
		this.version = version;
	}

}
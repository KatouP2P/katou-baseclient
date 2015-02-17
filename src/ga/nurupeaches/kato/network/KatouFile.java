package ga.nurupeaches.kato.network;

import ga.nurupeaches.kato.Configuration;
import ga.nurupeaches.kato.io.Chunk;
import ga.nurupeaches.kato.io.MemoryChunk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a file hosted/shared by KatouClients.
 */
public class KatouFile {

	private Map<Integer, Chunk> fileChunks = new HashMap<>();
	private RandomAccessFile file;
	private Path path;
	private KatouMetadata metadata;

	/**
	 * Constructs a KatouFile based on an existed file.
	 * @param path
	 */
	public KatouFile(Path path) throws IOException {
		if(!Files.exists(path)){
			throw new IllegalArgumentException("Attempted to create a KatouFile with a non-existent file!");
		}

		this(path.getFileName().toString(), )
	}

	public KatouFile(String name, String hash, long size) throws IOException {
		this(new KatouMetadata().setName(name).setSize(size).setHash(hash));
	}

	public KatouFile(KatouMetadata metadata) throws IOException {
		path = Paths.get(Configuration.getNode("defaultSaveLocation"), metadata.getName());
		file = new RandomAccessFile(path.toFile(), "rw");
		this.metadata = metadata;
	}


	public Path getSavePath(){
		return path;
	}

	public void flushMemoryChunk(int id) throws IOException {
		Chunk chunk = fileChunks.get(id);
		if(chunk == null || !(chunk instanceof MemoryChunk)){
			return;
		}

		ByteBuffer buffer = ((MemoryChunk)chunk).getBuffer();
		file.getChannel().write(buffer, chunk.getPosition());
	}

	@Override
	public boolean equals(Object other){
		if(!(other instanceof KatouFile)){
			return false;
		}

		return metadata.equals(((KatouFile)other).metadata);
	}

}
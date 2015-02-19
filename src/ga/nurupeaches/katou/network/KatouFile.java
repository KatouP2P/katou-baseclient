package ga.nurupeaches.katou.network;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.io.Chunk;
import ga.nurupeaches.katou.io.MemoryChunk;
import ga.nurupeaches.katou.utils.HashUtils;

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
	 * @throws IOException
	 */
	public KatouFile(Path path) throws IOException {
		this(path.getFileName().toString(), HashUtils.hexifyArray(HashUtils.computeHash(path)), Files.size(path));
	}

	/**
	 * Constructs a KatouFile based on raw passed parameters.
	 * @param name Name of file
	 * @param hash SHA-256 hash of file
	 * @param size Size of file
	 * @throws IOException
	 */
	public KatouFile(String name, String hash, long size) throws IOException {
		this(new KatouMetadata().setName(name).setSize(size).setHash(hash));
	}

	/**
	 * Constructs a KatouFile based on a KatouMetadata
	 * @param metadata The metadata to pass
	 * @throws IOException
	 */
	public KatouFile(KatouMetadata metadata) throws IOException {
		path = Paths.get(Configuration.getNode("defaultSaveLocation"), metadata.getName());
		file = new RandomAccessFile(path.toFile(), "rw");
		this.metadata = metadata;
	}

	/**
	 * Returns the save path for this file.
	 */
	public Path getSavePath(){
		return path;
	}

	/**
	 * Flushes an in-memory chunk to the file.
	 * @param id The chunk's ID.
	 * @throws IOException
	 */
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
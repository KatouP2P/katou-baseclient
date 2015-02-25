package ga.nurupeaches.katou.network;

import ga.nurupeaches.katou.io.Chunk;
import ga.nurupeaches.katou.io.MemoryChunk;
import ga.nurupeaches.katou.utils.HashUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a file hosted/shared by KatouClients.
 */
public class KatouFile {

	private Map<Integer, Chunk> fileChunks = new HashMap<Integer, Chunk>();
	private RandomAccessFile randomAccessFile;
	private File file;
	private Metadata metadata;

	/**
	 * Constructs a KatouFile based on an existed file.
	 * @param file
	 * @throws IOException
	 */
	public KatouFile(File file) throws IOException {
		this(file.getName(), HashUtils.hexifyArray(HashUtils.computeHash(file)), file.length());
	}

	/**
	 * Constructs a KatouFile based on raw passed parameters.
	 * @param name Name of file
	 * @param hash SHA-256 hash of file
	 * @param size Size of file
	 * @throws IOException
	 */
	public KatouFile(String name, String hash, long size) throws IOException {
		this(new Metadata().setName(name).setSize(size).setHash(hash));
	}

	/**
	 * Constructs a KatouFile based on a Metadata
	 * @param metadata The metadata to pass
	 * @throws IOException
	 */
	public KatouFile(Metadata metadata) throws IOException {
		//TODO: Re-implement getNode(String)
		file = new File(/*Configuration.getNode("defaultSaveLocation")*/"", metadata.getName());
		randomAccessFile = new RandomAccessFile(file, "rw");
		this.metadata = metadata;
	}

	/**
	 * Returns the save path for this file.
	 */
	public File getSavePath(){
		return file;
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
		randomAccessFile.getChannel().write(buffer, chunk.getPosition());
	}

	@Override
	public boolean equals(Object other){
		if(!(other instanceof KatouFile)){
			return false;
		}

		return metadata.equals(((KatouFile)other).metadata);
	}

}
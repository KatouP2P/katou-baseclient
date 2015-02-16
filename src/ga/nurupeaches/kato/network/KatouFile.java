package ga.nurupeaches.kato.network;

import ga.nurupeaches.kato.KatouClient;
import ga.nurupeaches.kato.io.Chunk;
import ga.nurupeaches.kato.io.MemoryChunk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

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
	public KatouFile(Path path){
		if(!Files.exists(path)){
			throw new IllegalArgumentException("Attempted to create a KatouFile with a non-existent file!");
		}
	}

	public KatouFile(String name, long size, Path path){
		this.path = path;
		metadata = new KatouMetadata();
		metadata.setName(name);
		metadata.setSize(size);


		try{
			this.file = new RandomAccessFile(path.toFile(), "rw");
		} catch (FileNotFoundException e){
			KatouClient.LOGGER.log(Level.SEVERE, "Failed to create new file for KatouFile@" + path + "!");
		}
	}


	public Path getPath(){
		return path;
	}

	public String getName(){
		return name;
	}

	public long getSize(){
		return size;
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
		return this == other;
	}

}
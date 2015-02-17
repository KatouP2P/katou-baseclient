package ga.nurupeaches.kato.utils;

import ga.nurupeaches.kato.KatouClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

public class HashUtils {

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private static final String DEFAULT_HASH = "SHA-256";
	private static MessageDigest DIGEST;

	static {
		try {
			DIGEST = MessageDigest.getInstance(DEFAULT_HASH);
		} catch (NoSuchAlgorithmException e){
			KatouClient.LOGGER.log(Level.WARNING, "Failed to obtain a digester for algorithm " + DEFAULT_HASH, e);
		}
	}

	public static byte[] computeHash(byte[] data){
		try{
			DIGEST.update(data);
			return DIGEST.digest();
		} finally {
			DIGEST.reset();
		}
	}

	/**
	 * Computes a string's hash.
	 * @param string The string to hash
	 * @return The digest in a byte array
	 */
	public static byte[] computeHash(String string){
		return computeHash(string.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Computes a hash for a path (uses streams as opposed to in-memory byte arrays).
	 * @param path Path (file) to compute
	 * @return The digest in a byte array.
	 */
	public static byte[] computeHash(Path path){
		DigestInputStream digestingStream = null;
		try(InputStream fileStream = Files.newInputStream(path)){
			digestingStream = new DigestInputStream(fileStream, DIGEST);
			while(digestingStream.read() != -1);
			return DIGEST.digest();
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.WARNING, "Failed to calculate hash for file " + path, e);
			return new byte[0];
		} finally {
			DIGEST.reset();
			if(digestingStream != null){
				try{
					digestingStream.close();
				} catch (IOException e){
					KatouClient.LOGGER.log(Level.WARNING, "Failed to close digest stream! We're leaking resources!", e);
				}
			}
		}
	}

	public static String hexifyArray(byte[] array){
		char[] hexified = new char[array.length * 2];
		for(int i = 0; i < array.length; i++){
			int v = array[i] & 0xFF;
			hexified[i * 2] = HEX_ARRAY[v >>> 4];
			hexified[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}

		return new String(hexified);
	}

}
package ga.nurupeaches.kato;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Configuration {

	/**
	 * The mapping of all the settings. Can contain settings Katou doesn't even use.
	 */
	private static final Map<String, String> SETTINGS = new HashMap<String, String>();

	static {
		// Load the default configuration.
		loadConfig();
	}

	/**
	 * Construction of a static-based object is invalid. Just saying.
	 */
	private Configuration(){}

	/**
	 * Loads the default
	 */
	private static void loadConfig(){
		loadConfig(Paths.get("katou.config"));
	}

	/**
	 * Loads the configuration file.
	 * @param config - The configuration file to load.
	 */
	private static void loadConfig(Path config){
		SETTINGS.clear(); // Flush out the old settings.

		try(FileChannel channel = FileChannel.open(config, StandardOpenOption.READ)){
			BufferedReader reader = new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8.newDecoder(), -1));

			reader.lines().forEach((line) -> {
				String[] option = line.split(":");
				SETTINGS.put(option[0], option[1]);
			});

			reader.close();
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.SEVERE, "Failed to load configuration file.", e);
		}
	}

	/**
	 * Returns a configured ndoe.
	 * @param key - The key to the node to fetch.
	 * @return The value associated with the key.
	 */
	public static String getNode(String key){
		return SETTINGS.get(key);
	}

}
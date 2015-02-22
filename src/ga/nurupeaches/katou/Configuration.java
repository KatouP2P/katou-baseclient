package ga.nurupeaches.katou;

import ga.nurupeaches.katou.network.manager.SocketType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Configuration {

	/**
	 * The mapping of all the settings. Can contain settings Katou doesn't even use.
	 */
	private static final Map<String, Object> SETTINGS = new HashMap<String, Object>();

	static {
		// Load the default configuration.
		loadConfig();
	}

	/**
	 * Construction of a heavily static object is wrong. Just saying.
	 */
	private Configuration(){}

	/**
	 * Loads the default
	 */
	public static void loadConfig(){
		loadConfig(new File("katou.config"));
	}

	/**
	 * Loads the configuration file.
	 * @param config - The configuration file to load.
	 */
	public static void loadConfig(File config){
		SETTINGS.clear(); // Flush out the old settings.

		if(!config.exists()){
			try{
				if(!config.createNewFile()){
					throw new IOException("exists() returned false but existed?");
				}

				loadDefaults();
			} catch (IOException e){
				e.printStackTrace();
			}
		}

		try{
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String l;
			while((l = reader.readLine()) != null){
				// # is used to start a comment.
				if(l.startsWith("#")){
					continue;
				}

				if(!l.contains(":")){
					KatouClient.LOGGER.log(Level.WARNING, "Line " + l + " is invalid.");
					continue;
				}

				String[] setting = l.split(":");
				if(setting.length < 1){
					KatouClient.LOGGER.log(Level.WARNING, "Line " + l + " is empty.");
					continue;
				}

				// Reserved for Katou.
				if(setting[0].equals("Port")){
					SETTINGS.put("Port", Integer.parseInt(setting[1]));
				} else if(setting[0].equals("SocketType")){
					SETTINGS.put("SocketType", SocketType.valueOf(setting[1]));
				} else if(setting[0].equals("CharacterSet")){
					SETTINGS.put("CharacterSet", Charset.forName(setting[1]));
				}
			}
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.SEVERE, "Failed to load configuration file.", e);
		}

	}

	/**
	 * Loads the default settings.
	 */
	public static void loadDefaults(){
		putIfAbsent(SETTINGS, "Port", 6800);
		putIfAbsent(SETTINGS, "SocketType", SocketType.TCP);
		putIfAbsent(SETTINGS, "CharacterSet", Charset.forName("UTF-8"));
	}

	private static <K, V> void putIfAbsent(Map<K, V> map, K key, V value){
		if(map.containsKey(key)){
			return;
		}

		map.put(key, value);
	}

	/**
	 * Returns the socket type to use.
	 * @return The socket type or TCP if was set ot default.
	 */
	public static SocketType getSocketType(){
		return (SocketType)SETTINGS.get("SocketType");
	}

	/**
	 * Returns the port to use.
	 * @return The port or 6800 if was set to default.
	 */
	public static int getPort(){
		return (Integer)SETTINGS.get("Port");
	}

	/**
	 * Returns the character set to use for parsing Strings.
	 * @return A character set or the UTF-8 charset if was set to default.
	 */
	public static Charset getCharset(){
		return (Charset)SETTINGS.get("CharacterSet");
	}

}
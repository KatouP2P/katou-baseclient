package ga.nurupeaches.katou;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

public final class Configuration {

	/**
	 * The mapping of all the settings. Can contain settings Katou doesn't even use.
	 */
	private static final Map<String, Object> SETTINGS = new HashMap<>(3);

	static {
		// Load the default configuration.
		loadConfig();
	}

	/**
	 * Construction of a heavily static object is wrong. Just saying.
	 */
	private Configuration(){}

    /**
     * Loads the config file.
     */
	public static void loadConfig(){
		loadConfig(Paths.get(System.getProperty("user.home"), "katou.config"));
	}

    /**
     * Saves the config file.
     */
    public static void saveConfig(){
        saveConfig(Paths.get(System.getProperty("user.home"), "katou.config"));
    }

	/**
	 * Loads the configuration file.
	 * @param config - The configuration file to load.
	 */
	private static void loadConfig(Path config){
		SETTINGS.clear(); // Flush out the old settings.

		if(!Files.exists(config)){
			try{
				Files.createFile(config);
				if(!Files.exists(config)){
					throw new IOException("exists() returned false but existed?");
				}
			} catch (IOException e){
				e.printStackTrace();
			}
		}

		try{
			Stream<String> stream = Files.lines(config);
			stream.forEach(str -> {
                if(str.isEmpty() || str.startsWith("#")){
                    return;
                }

                if(!str.contains("=")){
                    KatouClient.LOGGER.log(Level.WARNING, "Line " + str + " is invalid.");
                    return;
                }

                String[] setting = str.split("=");
                switch(setting[0].toLowerCase()){
                    case "port":
                        SETTINGS.put("Port", Integer.parseInt(setting[1]));
                        break;
                    case "characterset":
                        SETTINGS.put("CharacterSet", Charset.forName(setting[1]));
                        break;
                    case "buffersize":
                        SETTINGS.put("BufferSize", Integer.parseInt(setting[1]));
                        break;
                    case "defaultsavelocation":
                        Path saveLocation = Paths.get(setting[1]);
                        if(!Files.exists(saveLocation)){
                            try {
                                Files.createDirectories(saveLocation);
                            } catch(IOException e){
                                throw new RuntimeException("Failed to create save location!");
                            }
                        }
                        SETTINGS.put("DefaultSaveLocation", saveLocation);
                        break;
                }
            });
		} catch (IOException e){
			KatouClient.LOGGER.log(Level.SEVERE, "Failed to load configuration file.", e);
		}

        // Load defaults for missing settings
        loadDefaults();
	}

    private static void saveConfig(Path config){
        OutputStream stream = null;
        try {
            stream = Files.newOutputStream(config);

            for(Map.Entry<String, Object> obj : SETTINGS.entrySet()){
                String value;
                if(obj.getValue() instanceof Charset){
                    value = ((Charset) obj.getValue()).displayName(Locale.ENGLISH);
                } else {
                    value = obj.getValue().toString();
                }

                stream.write((obj.getKey() + '=' + value + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(stream != null){
                try {
                    stream.flush();
                    stream.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

	/**
	 * Loads the default settings.
	 */
	public static void loadDefaults(){
		putIfAbsent(SETTINGS, "Port", 6800);
        Path defaultSaveDir = Paths.get(System.getProperty("user.home"), "KatouDownloads");
		try {
            Files.createDirectories(defaultSaveDir);
        } catch (IOException e){
            KatouClient.LOGGER.log(Level.WARNING, "Failing to create default save directory!");
        }

        putIfAbsent(SETTINGS, "DefaultSaveLocation", defaultSaveDir);
        putIfAbsent(SETTINGS, "RecvBufferSize", 512);
        putIfAbsent(SETTINGS, "SendBufferSize", 512);
        System.out.println("Loaded defaults");
	}

	private static <K, V> void putIfAbsent(Map<K, V> map, K key, V value){
		if(map.containsKey(key)){
			return;
		}

		map.put(key, value);
	}

    /**
     * Returns the reciving buffer size.
     * @return Reciving buffer size
     */
    public static Integer getRecvBufferSize(){ return (Integer)SETTINGS.get("RecvBufferSize"); }

    /**
     * Returns the sending buffer size.
     * @return Sending buffer size
     */
    public static Integer getSendBufferSize(){ return (Integer)SETTINGS.get("SendBufferSize"); }

	/**
	 * Returns the port to use.
	 * @return The port or 6800 if was set to default.
	 */
	public static int getPort(){
		return (Integer)SETTINGS.get("Port");
	}

	/**
	 * Returns the default save location as a File object.
	 * @return A file representing the default save location.
	 */
	public static Path getDefaultSaveLocation(){
		return (Path)SETTINGS.get("DefaultSaveLocation");
	}

}
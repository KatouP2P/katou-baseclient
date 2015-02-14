package ga.nurupeaches.kato.utils;

import ga.nurupeaches.kato.KatouClient;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class UnsafeUtils {

	private static Unsafe ourUnsafe;

	static {
		if(ourUnsafe == null){
			try{
				Field field = Unsafe.class.getDeclaredField("theUnsafe");
				field.setAccessible(true);
				ourUnsafe = (Unsafe)field.get(null);
			} catch (ReflectiveOperationException e){
				KatouClient.LOGGER.log(Level.SEVERE, "Failed to obtain an instance of the Unsafe!", e);
			}
		}
	}

	public static Unsafe getUnsafe(){
		return ourUnsafe;
	}

}
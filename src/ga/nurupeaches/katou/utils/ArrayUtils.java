package ga.nurupeaches.katou.utils;

public class ArrayUtils {

	public static boolean contains(Object[] arr, Object expected){
		for(Object obj : arr){
			if(obj.equals(expected)){
				return true;
			}
		}

		return false;
	}

}
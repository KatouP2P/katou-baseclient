package ga.nurupeaches.kato.listening;

import ga.nurupeaches.kato.listening.events.Event;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

	private static final Map<Event, List<EventHandler>> EVENT_HANDLERS = new HashMap<>();

	public static void callEvent(Event event, ByteBuffer buffer){
		EVENT_HANDLERS.get(event).forEach((handler) -> {

			handler.onCall(event, buffer);
			buffer.flip();

		});
	}

}
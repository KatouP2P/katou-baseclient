package ga.nurupeaches.kato.listening.events;

public interface Event {

	/**
	 * Enum for when we want to talk to them.
	 */
	public enum UsToThem implements Event {

		GIVE_METADATA

	}

	/**
	 * Enum for when they want to talk to us.
	 */
	public enum ThemToUs implements Event {

		GIVE_METADATA

	}

}
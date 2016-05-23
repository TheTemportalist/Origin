package temportalist.origin.api.client;

/**
 * @author TheTemportalist  5/20/15
 */
public enum EnumKeyCategory {

	MOVEMENT("movement"),
	INVENTORY("inventory"),
	GAMEPLAY("gameplay"),
	MULTIPLAYER("multiplayer"),
	MISC("misc"),
	STREAM("stream");

	private final String name;

	EnumKeyCategory(String name) {
		this.name = "key.categories." + name.toLowerCase();
	}

	public String getName() {
		return this.name;
	}

}

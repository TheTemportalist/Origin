package temportalist.origin.api.common.tile;

public enum ActivatedAction {

	PULSE(new String[] {
			"Active once per redstone pulse (while settings apply)"
	}), WHILE(new String[] {
			"Active while redstone pulse (while settings apply)"
	});

	private final String[] description;

	ActivatedAction(String[] desc) {
		this.description = desc;
	}

	public static ActivatedAction getState(int stateID) {
		if (stateID < 0)
			return WHILE;
		else if (stateID == 0)
			return PULSE;
		else if (stateID == 1)
			return WHILE;
		else if (stateID > 1)
			return PULSE;
		else
			return null;
	}

	public static int getInt(ActivatedAction state) {
		if (state == PULSE)
			return 0;
		else if (state == WHILE)
			return 1;
		else
			return -1;
	}

}

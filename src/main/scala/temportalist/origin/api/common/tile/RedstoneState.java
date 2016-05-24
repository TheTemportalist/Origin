package temportalist.origin.api.common.tile;

public enum RedstoneState {
	IGNORE(new String[] {
			"Ignore any nearby redstone signals"
	}), LOW(new String[] {
			"Only active without a redstone signal"
	}), HIGH(new String[] {
			"Only active with a redstone signal"
	});

	private final String[] description;

	RedstoneState(String[] desc) {
		this.description = desc;
	}

	public static RedstoneState getStateFromInt(int stateID) {
		if (stateID < 0)
			return RedstoneState.HIGH;
		else if (stateID == 0)
			return RedstoneState.IGNORE;
		else if (stateID == 1)
			return RedstoneState.LOW;
		else if (stateID == 2)
			return RedstoneState.HIGH;
		else if (stateID > 2)
			return RedstoneState.IGNORE;
		else
			return null;
	}

	public static int getIntFromState(RedstoneState state) {
		if (state == RedstoneState.IGNORE)
			return 0;
		else if (state == RedstoneState.LOW)
			return 1;
		else if (state == RedstoneState.HIGH)
			return 2;
		else
			return -1;
	}

}

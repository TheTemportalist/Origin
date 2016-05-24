package com.temportalist.origin.screwdriver.api;

/**
 * Created by TheTemportalist on 12/20/2015.
 */
public enum BehaviorType {

	ACTIVE(0, "active", 96 - 26 + 9, 34 - 9, 4),
	PASSIVE(1, "passive", 186 - 22 + 9, 91 - 9, 4),
	TOGGLE(2, "toggle", 190 - 26 + 9, 34 - 9, 3);

	private final int id, guiStartX, guiStartY, guiRowSize;
	private final String key;

	BehaviorType(int id, String key, int startXGui, int startYGui, int rowSize) {
		this.id = id;
		this.key = key;
		this.guiStartX = startXGui;
		this.guiStartY = startYGui;
		this.guiRowSize = rowSize;
	}

	public int getID() {
		return this.id;
	}

	public String getKey() {
		return this.key;
	}

	public int getGuiStartX() {
		return this.guiStartX;
	}

	public int getGuiStartY() {
		return guiStartY;
	}

	public int getGuiRowSize() {
		return guiRowSize;
	}

}

package com.temportalist.origin.api.client.gui.widget;

import com.temportalist.origin.api.client.gui.IGuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class Component {

	protected final IGuiScreen owner;
	final int column, row;

	public Component(IGuiScreen ownerGui, int col, int row) {
		this.owner = ownerGui;
		this.column = col;
		this.row = row;

	}

	public int getDisplayColumn() {
		return this.column;
	}

	public int getDisplayRow() {
		return this.row;
	}

	public abstract void draw(IGuiScreen gui, int x, int y, int leftOffset, int rightOffset,
			int topOffset, int bottomOffset);

	public void onClick() {
	}

	public void onHover(List<String> hoverInfo) {
	}

}

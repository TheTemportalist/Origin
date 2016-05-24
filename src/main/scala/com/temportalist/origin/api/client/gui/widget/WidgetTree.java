package com.temportalist.origin.api.client.gui.widget;

import com.temportalist.origin.api.client.gui.IGuiScreen;
import com.temportalist.origin.api.client.utility.Rendering;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
public class WidgetTree {

	IWidgetOwner owner;
	GuiScreen parentScreen;
	final int boxX, boxY, boxW, boxH;

	int minColumn, maxColumn, minRow, maxRow;
	int innerBoxLeft, innerBoxRight;
	int innerBoxTop, innerBoxBottom;
	int innerBoxWidth, innerBoxHeight;
	int bufferW, bufferH;

	float scale = 1.0F;
	final ResourceLocation background;

	protected double prevMapPosX;
	protected double prevMapPosY;
	protected double mapPosX;
	protected double mapPosY;
	protected double savedMapPosX_maybe;
	protected double savedMapPosY_maybe;

	private int isMouseButtonDown;
	protected int mouseX_Saved;
	protected int mouseY_Saved;
	protected float some_float_value = 1.0F;

	List<Component> components = new ArrayList<Component>();

	public WidgetTree(IWidgetOwner owner, IGuiScreen parentScreen, int x, int y, int boxWidth,
			int boxHeight, int minCol, int maxCol, int minRow, int maxRow, int startingCol,
			int startingRow, ResourceLocation background) {
		this.owner = owner;
		this.parentScreen = (GuiScreen)parentScreen;
		this.boxX = x;
		this.boxY = y;
		this.boxW = boxWidth;
		this.boxH = boxHeight;

		this.updateBox(this.minColumn = minCol, this.maxColumn = maxCol, this.minRow = minRow,
				this.maxRow = maxRow);

		this.background = background;

		short offsetX = 141;
		short offsetY = 141;
		this.prevMapPosX = this.mapPosX = this.savedMapPosX_maybe = (double) (startingCol * 24
				- offsetX / 2 - 12);
		this.prevMapPosY = this.mapPosY = this.savedMapPosY_maybe = (double) (startingRow * 24
				- offsetY / 2);

	}

	public void addComponent(Component comp) {
		this.components.add(comp);
	}

	public void updateBox(int minCol, int maxCol, int minRow, int maxRow) {

		this.bufferW = 50;
		this.bufferH = 50;

		this.innerBoxLeft = minCol * 24 - this.bufferW;
		this.innerBoxRight = ((maxCol + 1) * 24) + this.bufferW;

		this.innerBoxWidth = this.innerBoxRight + this.bufferW;

		this.innerBoxTop = minRow * 24 - this.bufferH;
		this.innerBoxBottom = ((maxRow + 1) * 24) + this.bufferH;

		this.innerBoxHeight = this.innerBoxBottom + this.bufferH;

		if (this.innerBoxWidth > this.innerBoxHeight) {
			this.innerBoxHeight = this.innerBoxWidth;
			this.innerBoxBottom = this.innerBoxRight;
		}
		else {
			this.innerBoxWidth = this.innerBoxHeight;
			this.innerBoxRight += this.innerBoxBottom;
		}

	}

	public void updateWidget() {
		this.prevMapPosX = this.mapPosX;
		this.prevMapPosY = this.mapPosY;
		double mapX = this.savedMapPosX_maybe - this.mapPosX;
		double mapY = this.savedMapPosY_maybe - this.mapPosY;

		if (mapX * mapX + mapY * mapY < 4.0D) {
			this.mapPosX += mapX;
			this.mapPosY += mapY;
		}
		else {
			this.mapPosX += mapX * 0.85D;
			this.mapPosY += mapY * 0.85D;
		}
	}

	public void drawWidget(int mouseX, int mouseY, float rpt) {
		if (Mouse.isButtonDown(0)) {
			if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1) && mouseX >= this.boxX
					&& mouseX < this.boxX + this.boxW && mouseY >= this.boxY
					&& mouseY < this.boxY + this.boxH) {
				if (this.isMouseButtonDown == 0) {
					this.isMouseButtonDown = 1;
				}
				else {
					this.mapPosX -= (double) ((float) (mouseX - this.mouseX_Saved)
							* this.some_float_value);
					this.mapPosY -= (double) ((float) (mouseY - this.mouseY_Saved)
							* this.some_float_value);
					this.savedMapPosX_maybe = this.prevMapPosX = this.mapPosX;
					this.savedMapPosY_maybe = this.prevMapPosY = this.mapPosY;
				}

				this.mouseX_Saved = mouseX;
				this.mouseY_Saved = mouseY;
			}
		}
		else {
			this.isMouseButtonDown = 0;
		}
		/*
		int dWheel = Mouse.getDWheel();
		float f4 = this.some_float_value;

		if (dWheel < 0) {
			this.some_float_value += 0.25F;
		}
		else if (dWheel > 0) {
			this.some_float_value -= 0.25F;
		}

		this.some_float_value = MathHelper.clamp_float(this.some_float_value, 1.0F, 2.0F);

		if (this.some_float_value != f4) {
			int xSize = this.owner.getXSize();
			int ySize = this.owner.getYSize();
			float f5 = f4 * (float) xSize;
			float f1 = f4 * (float) ySize;
			float f2 = this.some_float_value * (float) xSize;
			float f3 = this.some_float_value * (float) ySize;
			this.mapPosX -= (double) ((f2 - f5) * 0.5F);
			this.mapPosY -= (double) ((f3 - f1) * 0.5F);
			this.savedMapPosX_maybe = this.prevMapPosX = this.mapPosX;
			this.savedMapPosY_maybe = this.prevMapPosY = this.mapPosY;
		}

		this.savedMapPosX_maybe = this.getWithin(this.savedMapPosX_maybe,
				(double) this.innerBoxLeft, (double) this.innerBoxRight);
		this.savedMapPosY_maybe = this.getWithin(this.savedMapPosY_maybe,
				(double) this.innerBoxTop, (double) this.innerBoxBottom);
		 */
		this.drawComponents(mouseX, mouseY, rpt);
	}

	protected void drawComponents(int mouseX, int mouseY, float rpt) {
		/*
		int currentMapPosX = MathHelper.floor_double(this.prevMapPosX
				+ (this.mapPosX - this.prevMapPosX) * (double) rpt);
		int currentMapPosY = MathHelper.floor_double(this.prevMapPosY
				+ (this.mapPosY - this.prevMapPosY) * (double) rpt);

		currentMapPosX = this.getWithin(currentMapPosX, this.innerBoxLeft, this.innerBoxRight
				- (this.innerBoxWidth - (this.innerBoxWidth - this.boxW)));
		currentMapPosY = this.getWithin(currentMapPosY, this.innerBoxTop, this.innerBoxBottom
				- (this.innerBoxHeight - (this.innerBoxHeight - this.boxH)));
		 */
		int currentMapPosX = this.getCurrentMapPosX(rpt);
		int currentMapPosY = this.getCurrentMapPosY(rpt);

		GL11.glDepthFunc(GL11.GL_GEQUAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) this.boxX, (float) this.boxY, 0.0F);
		GL11.glScalef(1.0F / this.some_float_value, 1.0F / this.some_float_value, 0.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		GL11.glPushMatrix();
		this.drawStaticBackground(currentMapPosX, currentMapPosY);
		// this.drawMovingBackground(currentMapPosX, currentMapPosY);
		GL11.glPopMatrix();

		int index;

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		int compX;
		int compY;

		for (index = 0; index < this.components.size(); ++index) {
			Component comp = this.components.get(index);
			compX = comp.getDisplayColumn() * 24 - currentMapPosX;
			compY = comp.getDisplayRow() * 24 - currentMapPosY;

			if (this.isWithinArea(compX, compY, -22, this.boxW, -22, this.boxH)) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

				int leftOffset = 0;
				if ((float) (compX) <= 0) {
					leftOffset = Math.abs(compX);
				}
				int rightOffset = 0;
				if ((float) (compX + 24) >= (float) this.boxW) {
					rightOffset = compX + 24 - this.boxW;
				}
				int topOffset = 0;
				if ((float) (compY) <= 0) {
					topOffset = Math.abs(compY);
				}
				int bottomOffset = 0;
				if ((float) (compY + 24) >= (float) this.boxH) {
					bottomOffset = compY + 24 - this.boxH;
				}

				comp.draw((IGuiScreen)this.parentScreen,
						compX - 2, compY - 2, leftOffset, rightOffset, topOffset, bottomOffset);

			}
		}

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glPopMatrix();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	private int getCurrentMapPosX(float rpt) {
		return this.getWithin(
				MathHelper.floor_double(this.prevMapPosX + (this.mapPosX - this.prevMapPosX)
						* (double) rpt), this.innerBoxLeft, this.innerBoxRight
						- (this.innerBoxWidth - (this.innerBoxWidth - this.boxW)));
	}

	private int getCurrentMapPosY(float rpt) {
		return this.getWithin(
				MathHelper.floor_double(this.prevMapPosY + (this.mapPosY - this.prevMapPosY)
						* (double) rpt), this.innerBoxTop, this.innerBoxBottom
						- (this.innerBoxHeight - (this.innerBoxHeight - this.boxH)));
	}

	private boolean isWithinArea(int x, int y, int left, int right, int top, int bottom) {
		return x >= left && y >= top && (float) x <= (float) right && (float) y <= (float) bottom;
	}

	private int getWithin(int pos, int min, int max) {
		return (int) this.getWithin((double) pos, (double) min, (double) max);
	}

	private double getWithin(double pos, double min, double max) {
		if (pos < min) {
			return min;
		}

		if (pos >= max) {
			return (max - 1);
		}

		return pos;
	}

	private void drawStaticBackground(int currentMapX, int currentMapY) {
		Rendering.bindResource(this.background);

		float scaleW = (float) (this.innerBoxWidth) / 256.0F;
		float scaleH = (float) (this.innerBoxHeight) / 256.0F;

		GL11.glScalef(scaleW, scaleH, 1.0F);

		this.parentScreen.drawTexturedModalRect(0, 0,
				(int) ((currentMapX + this.bufferW) / scaleW),
				(int) ((currentMapY + this.bufferH) / scaleH), (int) (this.boxW / scaleW) + 1,
				(int) (this.boxH / scaleH) + 1);

		GL11.glScalef(1.0F / scaleW, 1.0F / scaleH, 1.0F);
	}

	private void drawMovingBackground(int currentMapX, int currentMapY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int texWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
		int texHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

		float scaleW = 1.0F;
		float scaleH = 1.0F;

		int width = (this.innerBoxRight - this.innerBoxLeft);
		int height = (this.innerBoxBottom - this.innerBoxTop);
		int right = this.boxX + width;
		int bottom = this.boxY + height;
		int vx = (int) ((currentMapX - this.boxX) / Math.abs(this.boxX - bottom) * 288.0F);
		int vy = (int) ((currentMapY - this.boxY) / Math.abs(this.boxY - right) * 316.0F);
		GL11.glScalef(2.0F, 2.0F, 1.0F);
		Rendering.bindResource(this.background);

		this.parentScreen.drawTexturedModalRect(0, 0, vx / 2, vy / 2, this.boxW / 2, this.boxH / 2);
		GL11.glScalef(0.5F, 0.5F, 1.0F);

	}

	public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
		Component comp = this.getComponentAtMouse(mouseX, mouseY);
		if (comp != null)
			comp.onClick();
	}

	public void addHoverInformation(int mouseX, int mouseY, List<String> hoverInfo) {
		Component comp = this.getComponentAtMouse(mouseX, mouseY);
		if (comp != null)
			comp.onHover(hoverInfo);
	}

	private Component getComponentAtMouse(int mouseX, int mouseY) {
		if (this.isWithinArea(mouseX, mouseY, this.boxX, this.boxX + this.boxW, this.boxY,
				this.boxY + this.boxH)) {
			int currentMapPosX = this.getCurrentMapPosX(0);
			int currentMapPosY = this.getCurrentMapPosY(0);
			int compX;
			int compY;
			for (int i = 0; i < this.components.size(); i++) {
				Component comp = this.components.get(i);
				compX = comp.getDisplayColumn() * 24 - currentMapPosX;
				compY = comp.getDisplayRow() * 24 - currentMapPosY;

				if (!this.isWithinArea(compX, compY, -22, this.boxW, -22, this.boxH))
					continue;

				if (this.isWithinArea(mouseX, mouseY, this.boxX + compX, this.boxX + compX + 22,
						this.boxY + compY, this.boxY + compY + 22)) {
					return comp;
				}
			}
		}
		return null;
	}

}

package com.temportalist.origin.foundation.client.gui

import com.temportalist.origin.api.client.utility.Rendering
import com.temportalist.origin.internal.common.Origin
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiButtonCheck(id: Int, x: Int, y: Int, val isCheckmark: Boolean)
		extends GuiButton(id, x, y, 20, 20, "") {

	val texture: ResourceLocation = new
					ResourceLocation(Origin.MODID, "textures/gui/buttons.png")
	// Default Constructor

	// End Constructor

	// Other Constructors

	// End Constructors
	override def drawButton(minecraft: Minecraft, mouseX: Int, mouseY: Int): Unit = {
		if (this.visible) {
			Rendering.bindResource(this.texture)
			GL11.glColor3f(1.0F, 1.0F, 1.0F)
			val isHoveredOn: Boolean = mouseX >= this.xPosition && mouseY >= this.yPosition &&
					mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height
			val k: Int = this.getHoverState(isHoveredOn)

			var u: Int = 0
			var v: Int = 0

			if (this.isCheckmark) {
				u += 20
			}

			this.drawTexturedModalRect(this.xPosition, this.yPosition, 20 + u, k * 20, this.width,
				this.height)

		}
	}
}

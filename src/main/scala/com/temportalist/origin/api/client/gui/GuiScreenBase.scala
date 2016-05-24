package com.temportalist.origin.api.client.gui

import java.util

import com.temportalist.origin.api.common.utility.Generic
import net.minecraft.client.gui.{GuiButton, GuiScreen}

/**
 *
 *
 * @author TheTemportalist
 */
class GuiScreenBase() extends GuiScreen() with IGuiScreen {

	override protected def addButton(button: GuiButton): Unit = {
		Generic.addToList(this.buttonList, button)
	}

	override protected def renderHoverInformation(mouseX: Int, mouseY: Int,
			hoverInfo: util.List[String]): Unit = {
		this.func_146283_a(hoverInfo, mouseX, mouseY)
	}

	override def drawString(string: String, x: Int, y: Int, color: Int): Unit = {
		this.fontRendererObj.drawString(string, x, y, color)
	}

	override def getStringWidth(string: String): Int = {
		this.fontRendererObj.getStringWidth(string)
	}

}

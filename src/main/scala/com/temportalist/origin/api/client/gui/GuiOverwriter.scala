package com.temportalist.origin.api.client.gui

import cpw.mods.fml.relauncher.{SideOnly, Side}
import net.minecraft.client.gui.{GuiButton, GuiScreen}
import java.util

/**
 *
 *
 * @author  TheTemportalist  5/7/15
 */
@SideOnly(Side.CLIENT)
trait GuiOverwriter {

	def overwriteGui(gui: GuiScreen, list: util.List[_]): Unit

	def canClickButton(gui: GuiScreen, button: GuiButton): Boolean = true

	def onAction(gui: GuiScreen, button: GuiButton): Unit

}

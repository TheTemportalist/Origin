package temportalist.origin.foundation.client.gui

import java.util

import net.minecraft.client.gui.{GuiButton, GuiScreen}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author  TheTemportalist  5/7/15
 */
@SideOnly(Side.CLIENT)
trait GuiOverWriter {

	def overwriteGui(gui: GuiScreen, list: util.List[_]): Unit

	def canClickButton(gui: GuiScreen, button: GuiButton): Boolean = true

	def onAction(gui: GuiScreen, button: GuiButton): Unit

}

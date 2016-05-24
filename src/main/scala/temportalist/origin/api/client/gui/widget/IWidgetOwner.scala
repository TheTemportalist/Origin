package temportalist.origin.api.client.gui.widget

import net.minecraftforge.fml.relauncher.{SideOnly, Side}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
trait IWidgetOwner {

	def getXSize: Int

	def getYSize: Int

	def getGuiLeft: Int

	def getGuiTop: Int

}

package temportalist.origin.api.common.lib

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

/**
 *
 *
 * @author TheTemportalist
 */
trait IRadialSelection {

	@SideOnly(Side.CLIENT)
	def draw(mc: Minecraft, x: Double, y: Double, z: Double, w: Double, h: Double,
			renderText: Boolean): Unit

	def onSelection(player: EntityPlayer): Unit

	def getGlobalID: Int

}

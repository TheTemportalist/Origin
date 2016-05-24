package temportalist.origin.screwdriver.common.behaviors

import com.temportalist.origin.api.common.lib.IRadialSelection
import com.temportalist.origin.screwdriver.api.Behavior
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer

/**
 * Created by TheTemportalist on 12/23/2015.
 */
class BehaviorWrap(val behavior: Behavior) extends IRadialSelection {
	
	@SideOnly(Side.CLIENT)
	override def draw(mc: Minecraft, x: Double, y: Double, z: Double, w: Double, h: Double,
			renderText: Boolean): Unit = behavior.draw(mc, x, y, z, w, h, renderText)

	override def onSelection(player: EntityPlayer): Unit = {
		if (behavior.onSelection(player))
			AddonScrewdriver.NBTBehaviorHelper.setCurrentActiveBehavior(
				player.getCurrentEquippedItem, this.behavior)
	}

	override def getGlobalID: Int = this.behavior.getGlobalID

}

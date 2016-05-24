package temportalist.origin.screwdriver.client

import com.temportalist.origin.screwdriver.common.{AddonScrewdriver, ProxyCommon}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by TheTemportalist on 12/22/2015.
 */
class ProxyClient extends ProxyCommon {

	override def register(): Unit = {
		GuiScrewdriverRadial.registerClient()
	}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		if (ID == AddonScrewdriver.GUI_BEHAVIORS) {
			return new GuiBehaviors(player)
		}
		else if (ID == AddonScrewdriver.GUI_DATA_CORE) {
			return new GuiDataCore(player)
		}
		null
	}

}

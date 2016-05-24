package temportalist.origin.screwdriver.common

import com.temportalist.origin.api.common.proxy.IProxy
import com.temportalist.origin.screwdriver.client.GuiScrewdriverRadial
import com.temportalist.origin.screwdriver.common.container.ContainerBehaviors
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by TheTemportalist on 12/22/2015.
 */
class ProxyCommon extends IProxy {

	override def register(): Unit = {
		GuiScrewdriverRadial.register()
	}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = null

	override def getServerElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		if (ID == AddonScrewdriver.GUI_BEHAVIORS) {
			return new ContainerBehaviors(player)
		}
		null
	}

}

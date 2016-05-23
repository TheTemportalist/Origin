package temportalist.origin.internal.server

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import temportalist.origin.internal.common.ProxyCommon

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
class ProxyServer extends ProxyCommon {

	override def getServerElement(ID: Int, player: EntityPlayer,
			world: World, x: Int, y: Int, z: Int,
			tileEntity: TileEntity): AnyRef = {
		null
	}

}

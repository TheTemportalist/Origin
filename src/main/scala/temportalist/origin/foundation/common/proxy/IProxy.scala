package temportalist.origin.foundation.common.proxy

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import temportalist.origin.foundation.common.register.Register

/**
 *
 *
 * @author TheTemportalist
 */
trait IProxy extends IGuiHandler with Register.Unusual {

	def preInit(): Unit = {}

	def postInit(): Unit = {}

	override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int): AnyRef = {
		this.getClientElement(
			ID, player, world, x, y, z, world.getTileEntity(new BlockPos(x, y, z))
		)
	}

	override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int): AnyRef = {
		this.getServerElement(
			ID, player, world, x, y, z, world.getTileEntity(new BlockPos(x, y, z))
		)
	}

	def getClientElement(ID: Int, player: EntityPlayer, world: World,
			x: Int, y: Int, z: Int, tileEntity: TileEntity): AnyRef

	def getServerElement(ID: Int, player: EntityPlayer, world: World,
			x: Int, y: Int, z: Int, tileEntity: TileEntity): AnyRef

}

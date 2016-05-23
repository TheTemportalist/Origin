package temportalist.origin.foundation.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import temportalist.origin.foundation.common.registers.Register

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
trait IProxy extends IGuiHandler with Register.Unusual {

	def preInit(): Unit = {}

	override def register(): Unit = {}

	def postInit(): Unit = {}

	override def getClientGuiElement(ID: Int, player: EntityPlayer,
			world: World, x: Int, y: Int, z: Int): AnyRef = {
		this.getClientElement(ID, player, world, x, y, z, world.getTileEntity(new BlockPos(x, y, z)))
	}

	def getClientElement(ID: Int, player: EntityPlayer, world: World,
			x: Int, y: Int, z: Int, tileEntity: TileEntity): AnyRef

	override def getServerGuiElement(ID: Int, player: EntityPlayer,
			world: World, x: Int, y: Int, z: Int): AnyRef = {
		this.getServerElement(ID, player, world, x, y, z, world.getTileEntity(new BlockPos(x, y, z)))
	}

	def getServerElement(ID: Int, player: EntityPlayer, world: World,
			x: Int, y: Int, z: Int, tileEntity: TileEntity): AnyRef

}

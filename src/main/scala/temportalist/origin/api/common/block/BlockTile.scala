package temportalist.origin.api.common.block

import java.util.Random

import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.{IBlockAccess, World}
import temportalist.origin.api.common.tile.IPowerable
import temportalist.origin.foundation.common.register.BlockRegister

/**
 *
 *
 * @author TheTemportalist
 */
class BlockTile(reg: BlockRegister,
		private val tileClass: Class[_ <: TileEntity],
		itemBlock: Class[_ <: ItemBlock] = null,
		mat: Material = Material.ground, color: MapColor = null)
		extends BlockBase(reg, mat, color, itemBlock) {

	this.isBlockContainer = true

	override def createTileEntity(world: World, state: IBlockState): TileEntity = {
		if (this.hasTileEntity(state)) {
			try {
				// Try to create a new instance of this tile entity's class
				return this.tileClass.newInstance()
			}
			catch {
				case e: InstantiationException =>
					e.printStackTrace()
				case e: IllegalAccessException =>
					e.printStackTrace()
			}
		}
		null
	}

	override def hasTileEntity(state: IBlockState): Boolean = this.tileClass != null

	// ~~~~~~~~~~~~~~~ Start supered wrappers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def onBlockAdded(world: World, pos: BlockPos, state: IBlockState): Unit = {
		this.checkPower(world, pos)
		super.onBlockAdded(world, pos, state)
	}

	override def onNeighborChange(world: IBlockAccess, pos: BlockPos, neighbor: BlockPos): Unit = {
		try this.checkPower(world.asInstanceOf[World], pos)
		catch {case e: Exception =>}
		super.onNeighborChange(world, pos, neighbor)
	}

	override def updateTick(world: World, pos: BlockPos, state: IBlockState,
			rand: Random): Unit = {
		this.checkPower(world, pos)
		super.updateTick(world, pos, state, rand)
	}

	def checkPower(world: World, pos: BlockPos) {
		// make sure we are server side
		if (!world.isRemote) {
			// get the tile entity
			val tileEntity = world.getTileEntity(pos)
			// check if it is a valid powerable tile entity
			if (tileEntity != null && tileEntity.isInstanceOf[IPowerable]) {
				// cast and store in variable
				val powerable = tileEntity.asInstanceOf[IPowerable]
				// get if the world says this block is powered
				val blockPower = world.isBlockIndirectlyGettingPowered(pos)
				val blockGettingPower = blockPower > 0
				// set the powerable's power based on world power get ^
				if (powerable.isPowered(checkState = false) && !blockGettingPower) {
					powerable.setPowered(isRecievingPower = false)
				}
				else if (!powerable.isPowered(checkState = false) && blockGettingPower) {
					powerable.setPowered(isRecievingPower = true)
				}
			}
		}
	}

}

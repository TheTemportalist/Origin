package com.temportalist.origin.api.common.lib

import com.google.common.base.Objects
import com.temportalist.origin.api.common.utility.WorldHelper
import net.minecraft.block.Block
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk

/**
 *
 *
 * @author TheTemportalist
 */
class BlockCoord(_x: Int, _y: Int, _z: Int, var dim: Int) extends BlockPos(_x, _y, _z) {

	def this(vec: V3O, dim: Int) {
		this(vec.x_i(), vec.y_i(), vec.z_i(), dim)
	}

	def this(pos: BlockPos, dim: Int) {
		this(pos.getX, pos.getY, pos.getZ, dim)
	}

	def this(tile: TileEntity) {
		this(tile.xCoord, tile.yCoord, tile.zCoord, tile.getWorldObj.provider.dimensionId)
	}

	override def toString: String = {
		Objects.toStringHelper(this)
				.add("x", this.getX).add("y", this.getY).add("z", this.getZ).add("dim", this.dim).toString
	}

	override def equals(obj: scala.Any): Boolean = {
		obj match {
			case blockPos: BlockCoord =>
				super.equals(blockPos) && this.dim == blockPos.dim
			case _ => false
		}
	}

	override def hashCode(): Int = {
		super.hashCode() * 31 + this.dim
	}

	def getWorld: World = {
		WorldHelper.getWorld(this.dim)
	}

	def getBlockState: BlockState = new BlockState(this.getBlock, this.getBlockMeta(this.getWorld))

	def getBlock: Block = this.getWorld.getBlock(this.getX, this.getY, this.getZ)

	def getTile: TileEntity = this.getWorld.getTileEntity(this.getX, this.getY, this.getZ)

	def getChunk: Chunk = this.getWorld.getChunkFromBlockCoords(this.getX, this.getZ)

	def setBlock(blockState: BlockState, notify: Int): Unit = {
		this.setBlock(blockState.getBlock, blockState.getMeta, notify)
	}

	def setBlock(block: Block, meta: Int, notify: Int): Unit = {
		this.getWorld.setBlock(this.getX, this.getY, this.getZ, block, meta, notify)
	}

	def toCoord(x: Int, y: Int, z: Int): BlockCoord =
		new BlockCoord(this.getX, this.getY, this.getZ, this.dim)

	def notifyAllOfStateChange(): Unit = {
		this.getWorld.notifyBlocksOfNeighborChange(this.getX, this.getY, this.getZ, this.getBlock)
	}

	def notifyStateChange(): Unit = {
		this.getWorld.notifyBlockOfNeighborChange(this.getX, this.getY, this.getZ, this.getBlock)
	}

	def scheduleUpdate(delay: Int): Unit = {
		this.getWorld.scheduleBlockUpdate(this.getX, this.getY, this.getZ, this.getBlock, delay)
	}

	def scheduleUpdate(): Unit = this.scheduleUpdate(10)

	def markForUpdate(): Unit = this.getWorld.markBlockForUpdate(this.getX, this.getY, this.getZ)

}

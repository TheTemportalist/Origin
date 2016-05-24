package com.temportalist.origin.api.common.lib

import com.temportalist.origin.api.common.utility.WorldHelper
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist 3/29/15
 */
class BlockState(private var block: Block, private var meta: Int) {

	def this(tile: TileEntity) {
		this(tile.getBlockType, tile.getBlockMetadata)
	}

	def getBlock: Block = this.block

	def setBlock(b: Block): Unit = this.block = b

	def getMeta: Int = this.meta

	def setMeta(m: Int): Unit = this.meta = m

	def setInWorld(world: World, pos: BlockPos): Unit =
		pos.setBlock(world, this.block, this.meta, 3)

	def toStack: ItemStack = new ItemStack(this.getBlock, 1, this.getMeta)

	def toName: String = NameParser.getName(this, hasID = true, hasMeta = true)

}
object BlockState {

	def getState(stack: ItemStack): BlockState = {
		if (WorldHelper.isBlock(stack.getItem))
			new BlockState(Block.getBlockFromItem(stack.getItem), stack.getItemDamage)
		else null
	}

	def getState(name: String): BlockState = NameParser.getState(name)

}

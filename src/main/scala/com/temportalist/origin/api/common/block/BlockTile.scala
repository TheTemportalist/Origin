package com.temportalist.origin.api.common.block

import java.util.Random

import com.temportalist.origin.api.common.tile.IPowerable
import com.temportalist.origin.api.common.utility.WorldHelper
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class BlockTile(material: Material, pluginID: String, name: String,
		itemBlock: Class[_ <: ItemBlock], val tileEntityClass: Class[_ <: TileEntity])
		extends BlockBase(material, pluginID, name, itemBlock) {

	// Default Constructor
	this.isBlockContainer = true

	// End Constructor

	// Other Constructors
	def this(material: Material, pluginID: String, name: String,
			tileEntityClass: Class[_ <: TileEntity]) {
		this(material, pluginID, name, null, tileEntityClass)

	}

	def this(pluginID: String, name: String, itemBlock: Class[_ <: ItemBlock],
			tileEntityClass: Class[_ <: TileEntity]) {
		this(Material.ground, pluginID, name, itemBlock, tileEntityClass)

	}

	def this(pluginID: String, name: String, tileEntityClass: Class[_ <: TileEntity]) {
		this(pluginID, name, null, tileEntityClass)

	}

	// End Constructors

	override def createTileEntity(world: World, metadata: Int): TileEntity = {
		if (this.hasTileEntity(metadata)) {
			try {
				// Try to create a new instance of this tile entity's class
				return this.tileEntityClass.newInstance()
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

	override def hasTileEntity(metadata: Int): Boolean = this.tileEntityClass != null


	// ~~~~~~~~~~~~~~~ Start supered wrappers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def onBlockAdded(worldIn: World, x: Int, y: Int, z: Int): Unit = {
		// Check the power as soon as self is added to world
		this.checkPower(worldIn, x, y, z)
		super.onBlockAdded(worldIn, x, y, z)
	}

	override def onNeighborBlockChange(worldIn: World, x: Int, y: Int, z: Int,
			neighbor: Block): Unit = {
		// check the powewr when nearby blocks change
		this.checkPower(worldIn, x, y, z)
		super.onNeighborBlockChange(worldIn, x, y, z, neighbor)
	}

	override def updateTick(worldIn: World, x: Int, y: Int, z: Int, random: Random): Unit = {
		// check the power when updates are scheduled
		this.checkPower(worldIn, x, y, z)
		super.updateTick(worldIn, x, y, z, random)
	}

	def checkPower(world: World, x: Int, y: Int, z: Int) {
		// make sure we are server side
		if (!world.isRemote) {
			// get the tile entity
			val tileEntity: TileEntity = world.getTileEntity(x, y, z)
			// check if it is a valid powerable tile entity
			if (tileEntity != null && tileEntity.isInstanceOf[IPowerable]) {
				// cast and store in variable
				val powerable: IPowerable = tileEntity.asInstanceOf[IPowerable]
				// get if the world says this block is powered
				val blockGettingPower: Boolean = world.isBlockIndirectlyGettingPowered(x, y, z)
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

	def isClient(): Boolean = WorldHelper.isClient
	def isServer(): Boolean = WorldHelper.isServer

}

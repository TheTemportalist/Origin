package com.temportalist.origin.api.common.block

import java.util

import com.temportalist.origin.api.common.lib.{BlockState, V3O}
import com.temportalist.origin.api.common.rendering.IRenderingObject
import com.temportalist.origin.api.common.utility.Stacks
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * A wrapper class for the Minecraft Block.
 *
 * @param material
 * The block material (@see Material)
 * @param modid
 * The plugin id for the owner plugin
 * @param name
 * The name of the block
 * @param itemBlock
 * The item block class
 *
 * @author TheTemportalist
 */
class BlockBase(material: Material, val modid: String, name: String,
		itemBlock: Class[_ <: ItemBlock]) extends Block(material) with IRenderingObject {

	this.setBlockName(name)
	if (itemBlock != null) {
		GameRegistry.registerBlock(this, itemBlock, name)
	}
	else {
		GameRegistry.registerBlock(this, name)
	}

	// Other Constructors
	def this(material: Material, pluginID: String, name: String) {
		this(material, pluginID, name, null)
	}

	def this(pluginID: String, name: String, itemBlock: Class[_ <: ItemBlock]) {
		this(Material.ground, pluginID, name, itemBlock)
	}

	def this(pluginID: String, name: String) {
		this(pluginID, name, null)
	}

	// End Constructors

	override def getCompoundName: String = this.modid + ":" + this.name

	@SideOnly(Side.CLIENT)
	override def registerBlockIcons(reg: IIconRegister): Unit = {
		this.blockIcon = reg.registerIcon(this.getCompoundName)
	}

	/**
	 * Get the non-local name of this block
	 * @return
	 */
	override def getUnlocalizedName: String = {
		// return a formatted string using the format:
		//   tile.{pluginID}:{blockName}
		"tile." + this.getCompoundName
	}

	// ~~~~~~~~~~~~~~~ Start supered wrappers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def onBlockActivated(worldIn: World, x: Int, y: Int, z: Int, player: EntityPlayer,
			side: Int, subX: Float, subY: Float, subZ: Float): Boolean = {
		super.onBlockActivated(worldIn, x, y, z, player, side, subX, subY, subZ)
	}

	override def removedByPlayer(world: World, player: EntityPlayer, x: Int, y: Int, z: Int,
			willHarvest: Boolean): Boolean = {
		val pos: V3O = new V3O(x, y, z)
		if (!player.capabilities.isCreativeMode)
			Stacks.spawnDrops(world, pos,
				this.getDrops_Pre(world, pos, pos.getBlockState(world), pos.getTile(world))
			)
		super.removedByPlayer(world, player, x, y, z, willHarvest)
	}

	def getDrops_Pre(world: World, pos: V3O, state: BlockState,
			tile: TileEntity): util.List[ItemStack] = {
		super.getDrops(world, pos.x_i(), pos.y_i(), pos.z_i(), state.getMeta, 0)
	}

	/* Runs on POST block destruction */
	override def getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int,
			fortune: Int): util.ArrayList[ItemStack] = new util.ArrayList[ItemStack]()


}

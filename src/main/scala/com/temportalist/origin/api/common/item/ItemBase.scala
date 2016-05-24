package com.temportalist.origin.api.common.item

import java.util

import com.temportalist.origin.api.common.rendering.IRenderingObject
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.world.World

/**
 * A wrapper for Minecraft's Item
 *
 * @param modid
 * The plugin id of the owner plugin
 * @param name
 * The name of the item
 *
 * @author TheTemportalist
 */
class ItemBase(val modid: String, name: String) extends Item with IRenderingObject {

	this.setUnlocalizedName(name)
	GameRegistry.registerItem(this, name)

	override def getCompoundName: String = this.modid + ":" + this.name

	@SideOnly(Side.CLIENT)
	override def registerIcons(reg: IIconRegister): Unit = {
		this.itemIcon = reg.registerIcon(this.getCompoundName)
	}

	/**
	 * Get the non-local name of this item
	 * @return
	 */
	override def getUnlocalizedName: String = {
		// return a formatted string using the format:
		//   item.{pluginID}:{itemName}
		"item." + this.getCompoundName
	}

	/**
	 * Another way to get the unlocalized name of this item, while being sensitive to metadata
	 * @param itemStack
	 * @return
	 */
	override def getUnlocalizedName(itemStack: ItemStack): String = {
		this.getUnlocalizedName
	}

	// ~~~~~~~~~~~~~~~ Start supered wrappers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed.
	 *
	 * @param itemStack
	 * @param world
	 * @param player
	 * @return
	 */
	override def onItemRightClick(itemStack: ItemStack, world: World,
			player: EntityPlayer): ItemStack = {
		super.onItemRightClick(itemStack, world, player)
	}

	override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ)
	}

	/**
	 * Returns true if the item can be used on the given entity, e.g. shears on sheep.
	 *
	 * @param itemStack
	 * @param player
	 * @param entity
	 * @return
	 */
	override def itemInteractionForEntity(itemStack: ItemStack, player: EntityPlayer,
			entity: EntityLivingBase): Boolean = {
		super.itemInteractionForEntity(itemStack, player, entity)
	}

	/**
	 * Called when the player Left Clicks (attacks) an entity.
	 * Processed before damage is done, if return value is true further processing is canceled
	 * and the entity is not attacked.
	 *
	 * @param itemStack The Item being used
	 * @param player The player that is attacking
	 * @param entity The entity being attacked
	 * @return True to cancel the rest of the interaction.
	 */
	override def onLeftClickEntity(itemStack: ItemStack, player: EntityPlayer,
			entity: Entity): Boolean = {
		super.onLeftClickEntity(itemStack, player, entity)
	}

	/**
	 * Called when item is crafted/smelted. Used only by maps so far.
	 *
	 * @param itemStack
	 * @param world
	 * @param player
	 */
	override def onCreated(itemStack: ItemStack, world: World, player: EntityPlayer) {
	}

	/**
	 * Called each tick as long the item is on a player inventory. Uses by maps to check
	 * if is on a player hand and update it's contents.
	 *
	 * @param itemStack
	 * @param world
	 * @param entity
	 * @param slot
	 * @param isCurrentItem
	 */
	override def onUpdate(itemStack: ItemStack, world: World, entity: Entity, slot: Int,
			isCurrentItem: Boolean): Unit = {
	}

	/**
	 * allows items to add custom lines of information to the mouseover description
	 *
	 * @param itemStack
	 * @param player
	 * @param list
	 * @param isAdvanced
	 */
	@SideOnly(Side.CLIENT)
	override def addInformation(itemStack: ItemStack, player: EntityPlayer, list: util.List[_],
			isAdvanced: Boolean): Unit = {
		super.addInformation(itemStack, player, list, isAdvanced)
	}

}

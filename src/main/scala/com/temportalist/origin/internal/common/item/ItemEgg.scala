package com.temportalist.origin.internal.common.item

import java.util

import com.temportalist.origin.api.common.item.ItemPlacer
import com.temportalist.origin.api.common.utility.Generic
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity._
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.IIcon

/**
 *
 *
 * @author TheTemportalist 1/26/15
 */
class ItemEgg(modid: String, name: String) extends ItemPlacer(modid, name, null) {

	this.setCreativeTab(CreativeTabs.tabMisc)
	this.setHasSubtypes(true)

	@SideOnly(Side.CLIENT)
	private var overlayIcon: IIcon = _

	@SideOnly(Side.CLIENT)
	override def getSubItems(itemIn: Item, tab: CreativeTabs, subItems: util.List[_]): Unit = {
		for (i <- 0 until ItemEgg.classes.size()) {
			val stack: ItemStack = new ItemStack(itemIn)
			val tag: NBTTagCompound = new NBTTagCompound
			tag.setString("EntityName",
				EntityList.classToStringMapping.get(ItemEgg.classes.get(i)).toString
			)
			stack.setTagCompound(tag)
			Generic.addToList(subItems, stack)
		}
	}

	@SideOnly(Side.CLIENT)
	override def requiresMultipleRenderPasses: Boolean = true

	/**
	 * Gets an icon index based on an item's damage value and the given render pass
	 */
	@SideOnly(Side.CLIENT)
	override def getIconFromDamageForRenderPass(damage: Int, pass: Int): IIcon = {
		if (pass > 0) this.overlayIcon else this.itemIcon
	}

	@SideOnly(Side.CLIENT)
	override def registerIcons(reg: IIconRegister): Unit = {
		this.itemIcon = Items.spawn_egg.getIconFromDamageForRenderPass(0, 0)
		this.overlayIcon = Items.spawn_egg.getIconFromDamageForRenderPass(0, 1)
	}

	override def getEntityName(stack: ItemStack): String =
		if (stack.hasTagCompound) stack.getTagCompound.getString("EntityName") else "Unknown"

	@SideOnly(Side.CLIENT)
	override def getColorFromItemStack(stack: ItemStack, renderPass: Int): Int = {
		if (stack.hasTagCompound) {
			val entity: Class[_ <: Entity] =
				EntityList.stringToClassMapping.get(
					stack.getTagCompound.getString("EntityName")
				).asInstanceOf[Class[_ <: Entity]]
			renderPass match {
				case 0 => return ItemEgg.primary.get(entity)
				case _ => return ItemEgg.secondary.get(entity)
			}
		}
		16777215
	}

}

object ItemEgg {

	private val classes: util.List[Class[_ <: Entity]] = new
					util.ArrayList[Class[_ <: Entity]]()
	private val primary: util.HashMap[Class[_ <: Entity], Int] =
		new util.HashMap[Class[_ <: Entity], Int]()
	private val secondary: util.HashMap[Class[_ <: Entity], Int] =
		new util.HashMap[Class[_ <: Entity], Int]()

	def register(entity: Class[_ <: Entity], primary: Int, secondary: Int): Unit = {
		this.classes.add(entity)
		this.primary.put(entity, primary)
		this.secondary.put(entity, secondary)
	}

}

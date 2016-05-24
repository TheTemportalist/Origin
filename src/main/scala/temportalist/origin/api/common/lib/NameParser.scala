package temportalist.origin.api.common.lib

import java.util

import net.minecraft.block.Block
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.{GameData, GameRegistry}
import net.minecraftforge.oredict.OreDictionary
import temportalist.origin.api.common.utility.WorldHelper

/**
 *
 *
 * @author TheTemportalist
 */
object NameParser {

	def getName(stack: ItemStack): String = this.getName(stack, hasID = true, hasMeta = true)

	def getName(itemStack: ItemStack, hasID: Boolean, hasMeta: Boolean): String = {
		if (itemStack == null) {
			return ""
		}

		val fullname: ResourceLocation =
			if (!WorldHelper.isBlock(itemStack.getItem)) {
				GameData.getItemRegistry.getNameForObject(
					itemStack.getItem
				)
			}
			else {
				GameData.getBlockRegistry.getNameForObject(
					Block.getBlockFromItem(itemStack.getItem)
				)
			}
		(if (hasID) fullname.getResourceDomain + ":" else "") + fullname.getResourcePath +
				(if (hasMeta) ":" + itemStack.getItemDamage else "")
	}

	def getQualifiers(name: String): (String, String, Int) = {
		if (!name.matches("(.*):(.*)")) return null
		var endNameIndex: Int = name.length
		var metadata: Int = OreDictionary.WILDCARD_VALUE

		if (name.matches("(.*):(.*):(.*)")) {
			endNameIndex = name.lastIndexOf(':')
			metadata = name.substring(endNameIndex + 1, name.length()).toInt
		}

		val modid: String = name.substring(0, name.indexOf(':'))
		val itemName: String = name.substring(name.indexOf(':') + 1, endNameIndex)
		(modid, itemName, metadata)
	}

	def getItemStack(name: String): ItemStack = {
		this.getItemStack(this.getQualifiers(name))
	}

	def getItemStack(qualifiers: (String, String, Int)): ItemStack = {
		if (qualifiers == null || qualifiers._1 == null || qualifiers._2 == null) return null
		val block: Block = GameRegistry.findBlock(qualifiers._1, qualifiers._2)
		val item: Item = GameRegistry.findItem(qualifiers._1, qualifiers._2)
		if (block != null && Item.getItemFromBlock(block) != null)
			new ItemStack(block, 1, qualifiers._3)
		else if (item != null) new ItemStack(item, 1, qualifiers._3)
		else null
	}

	/*
	def getState(name: String): IBlockState = {
		val stack: ItemStack = this.getItemStack(name)
		if (stack == null) return null
		val block: Block = Block.getBlockFromItem(stack.getItem)
		if (block != null) block.getBlockState
		else null
	}
	*/

	def isInCollection(itemStack: ItemStack, list: util.Collection[String]): Boolean = {
		list.contains(this.getName(itemStack, hasID = true, hasMeta = false)) ||
				list.contains(this.getName(itemStack, hasID = true, hasMeta = true))
	}

}

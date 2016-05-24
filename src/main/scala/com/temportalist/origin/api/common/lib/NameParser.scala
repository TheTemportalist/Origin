package com.temportalist.origin.api.common.lib

import java.util

import com.temportalist.origin.api.common.utility.WorldHelper
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier
import cpw.mods.fml.common.registry.{GameData, GameRegistry}
import net.minecraft.block.Block
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.oredict.OreDictionary

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

		val fullname: String =
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
		val ui: UniqueIdentifier = new UniqueIdentifier(fullname)
		(if (hasID) ui.modId + ":" else "") + ui.name +
				(if (hasMeta) ":" + itemStack.getItemDamage else "")
	}

	def getName(state: BlockState, hasID: Boolean, hasMeta: Boolean): String = {
		this.getName(new ItemStack(state.getBlock, 1, state.getMeta), hasID, hasMeta)
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

	def getState(name: String): BlockState = {
		val stack: ItemStack = this.getItemStack(name)
		if (stack == null) return null
		val block: Block = Block.getBlockFromItem(stack.getItem)
		if (block != null) new BlockState(block, stack.getItemDamage)
		else null
	}

	def isInCollection(itemStack: ItemStack, list: util.Collection[String]): Boolean = {
		list.contains(this.getName(itemStack, hasID = true, hasMeta = false)) ||
				list.contains(this.getName(itemStack, hasID = true, hasMeta = true))
	}

}

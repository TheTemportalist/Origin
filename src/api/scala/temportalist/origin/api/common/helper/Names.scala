package temportalist.origin.api.common.helper

import java.util

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.oredict.OreDictionary

/**
  * Parses to and from block/item string names
  *
  * Created by TheTemportalist on 4/9/2016.
  * @author TheTemportalist
  */
object Names {

	/**
	  * Get the name for a block state
	  * @param state The state
	  * @param hasID If the mod ID should be included
	  * @param hasMeta If metadata should be included
	  * @return The string representation. Formatted as "id:name:meta"
	  */
	def getName(state: IBlockState, hasID: Boolean, hasMeta: Boolean): String = {
		this.getName(new ItemStack(state.getBlock, 1, state.getBlock.getMetaFromState(state)),
			hasID, hasMeta)
	}

	/**
	  * Get the name for an ItemStack
	  * @param itemStack The ItemStack
	  * @param hasID If the mod ID should be included
	  * @param hasMeta If metadata should be included
	  * @return The string representation. Formatted as "id:name:meta"
	  */
	def getName(itemStack: ItemStack, hasID: Boolean = true, hasMeta: Boolean = true): String = {
		if (itemStack == null) return ""

		val ui: ResourceLocation =
			if (!Objects.isBlock(itemStack.getItem))
				Item.REGISTRY.getNameForObject(itemStack.getItem)
			else
				Block.REGISTRY.getNameForObject(Block.getBlockFromItem(itemStack.getItem))

		(if (hasID) ui.getResourceDomain + ":" else "") + ui.getResourcePath +
				(if (hasMeta) ":" + itemStack.getItemDamage else "")
	}

	private def getQualifiers(name: String): (ResourceLocation, Int) = {
		if (!name.matches("(.*):(.*)")) return null
		var endNameIndex: Int = name.length
		var metadata: Int = OreDictionary.WILDCARD_VALUE

		if (name.matches("(.*):(.*):(.*)")) {
			endNameIndex = name.lastIndexOf(':')
			metadata = name.substring(endNameIndex + 1, name.length()).toInt
		}

		val modid: String = name.substring(0, name.indexOf(':'))
		val itemName: String = name.substring(name.indexOf(':') + 1, endNameIndex)
		(new ResourceLocation(modid, itemName), metadata)
	}

	/**
	  * Creates an ItemStack from a name passed
	  * @param name The name. Format should be "id:name:meta", where meta is optional
	  * @return The ItemStack representation. Can be null.
	  */
	def getItemStack(name: String): ItemStack = {
		val qualifiers = this.getQualifiers(name)
		if (qualifiers == null || qualifiers._1 == null) return null

		val block = Block.REGISTRY.getObject(qualifiers._1)
		val item = Item.REGISTRY.getObject(qualifiers._1)
		if (block != null && Item.getItemFromBlock(block) != null)
			new ItemStack(block, 1, qualifiers._2)
		else if (item != null) new ItemStack(item, 1, qualifiers._2)
		else null
	}

	/**
	  * Creates a IBlockState from a name passed
	  * @param name The name. Format should be "id:name:meta", where meta is optional
	  * @return The IBlockState representation. Can be null.
	  */
	def getState(name: String): IBlockState = {
		val qualifiers = this.getQualifiers(name)
		if (qualifiers == null || qualifiers._1 == null) return null

		val block = Block.REGISTRY.getObject(qualifiers._1)
		if (block != null) block.getStateFromMeta(qualifiers._2)
		else null
	}

	/**
	  * Checks if an ItemStack's fully qualified name is in a collection
	  * @param itemStack The ItemStack
	  * @param list The collection
	  * @return If "id:itemStackName" or "id:itemStackName:itemStack.getItemDamage" is in the collection
	  */
	def isInCollection(itemStack: ItemStack, list: util.Collection[String]): Boolean = {
		list.contains(this.getName(itemStack, hasID = true, hasMeta = false)) ||
				list.contains(this.getName(itemStack, hasID = true, hasMeta = true))
	}

}

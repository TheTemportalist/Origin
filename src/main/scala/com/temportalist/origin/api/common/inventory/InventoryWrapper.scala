package com.temportalist.origin.api.common.inventory

import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}

/**
 *
 *
 * @author TheTemportalist
 */
class InventoryWrapper(var name: String, var inventorySize: Int, var ownerStack: ItemStack)
		extends IInv {

	if (this.ownerStack != null && !this.ownerStack.hasTagCompound) {
		this.ownerStack.setTagCompound(new NBTTagCompound)
	}

	def this(title: String, inventorySize: Int) {
		this(title, inventorySize, null)

	}

	override def getInventoryName: String = this.name

	def isItemInventory: Boolean = {
		this.ownerStack != null
	}

	def markDirty() {
		for (i <- 0 until this.getSizeInventory) {
			if (this.getStackInSlot(i) != null && this.getStackInSlot(i).stackSize == 0)
				this.setInventorySlotContents(i, null)
		}
	}

	/**
	 * A custom method to read our inventory from an ItemStack's NBT compound
	 */
	def readFromNBT(compound: NBTTagCompound) {
		val items: NBTTagList = compound.getTagList("ItemInventory", 10)
		for (i <- 0 until items.tagCount()) {
			val item: NBTTagCompound = items.getCompoundTagAt(i)
			val slot: Byte = item.getByte("Slot")
			if (slot >= 0 && slot < getSizeInventory) {
				setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item))
			}
		}
	}

	/**
	 * A custom method to write our inventory to an ItemStack's NBT compound
	 */
	def writeToNBT(tagcompound: NBTTagCompound) {
		val nbttaglist: NBTTagList = new NBTTagList
		for (i <- 0 until this.getSizeInventory) {
			if (this.getStackInSlot(i) != null) {
				val nbttagcompound1: NBTTagCompound = new NBTTagCompound
				nbttagcompound1.setInteger("Slot", i)
				this.getStackInSlot(i).writeToNBT(nbttagcompound1)
				nbttaglist.appendTag(nbttagcompound1)
			}
		}
		tagcompound.setTag("ItemInventory", nbttaglist)
	}

}

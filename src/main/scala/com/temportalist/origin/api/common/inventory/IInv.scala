package com.temportalist.origin.api.common.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}

/**
 *
 *
 * @author TheTemportalist
 */
trait IInv extends IInventory with ISidedInventory {

	protected var slots: Array[ItemStack] = null
	protected var stackSize: Int = 64

	protected def setSlots(size: Int, maxStackSize: Int): Unit = {
		this.slots = new Array[ItemStack](size)
		this.stackSize = maxStackSize
	}

	protected def setSlots(size: Int): Unit = {
		this.setSlots(size, 64)
	}

	def clear(): Unit = {
		for (i <- 0 until this.getSizeInventory) this.slots(i) = null
	}

	override def getInventoryStackLimit: Int = this.stackSize

	override def getSizeInventory: Int = this.slots.length

	def hasInventory(): Boolean = this.slots != null

	def isValidSlot(index: Int): Boolean =
		true //this.hasInventory() && MathFuncs.between(0, index, this.getSizeInventory)

	override def getStackInSlot(index: Int): ItemStack = {
		if (this.isValidSlot(index)) {
			this.slots(index)
		}
		else null
	}

	override def getStackInSlotOnClosing(index: Int): ItemStack = this.getStackInSlot(index)

	override def setInventorySlotContents(slot: Int, stack: ItemStack): Unit = {
		this.slots(slot) = stack
		if (stack != null && stack.stackSize > getInventoryStackLimit)
			stack.stackSize = getInventoryStackLimit
		this.onStackChange(slot)
		this.markDirty()
	}

	override def decrStackSize(slot: Int, decrement: Int): ItemStack = {
		if (this.isValidSlot(slot)) {
			if (this.slots(slot) == null) return null
			val amount: Int =
				if (this.slots(slot).stackSize <= decrement) this.slots(slot).stackSize
				else decrement
			val localItemStack: ItemStack = this.slots(slot).splitStack(amount)
			if (this.slots(slot).stackSize <= 0) {
				this.slots(slot) = null
			}
			this.onStackChange(slot)
			return localItemStack
		}
		// return nothing, since nothing was taken
		null
	}

	def onStackChange(slot: Int): Unit = {}

	override def openInventory(): Unit = {}

	override def closeInventory(): Unit = {}

	override def isUseableByPlayer(playerIn: EntityPlayer): Boolean = true

	override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = this.isValidSlot(index)

	override def getAccessibleSlotsFromSide(side: Int): Array[Int] =
		if (this.hasInventory()) {
			val slotsFromSide: Array[Int] = new Array[Int](this.getSizeInventory)
			for (i <- 0 to this.getSizeInventory) {
				slotsFromSide(i) = i
			}
			slotsFromSide
		}
		else
			null

	override def canInsertItem(
			index: Int, itemStackIn: ItemStack, direction: Int): Boolean =
		this.isValidSlot(index)

	override def canExtractItem(index: Int, stack: ItemStack, direction: Int): Boolean =
		this.isValidSlot(index)

	def writeNBT_Inv(): NBTTagCompound = {
		val tagCom: NBTTagCompound = new NBTTagCompound
		this.writeNBT_IInv(tagCom)
		tagCom
	}

	def writeNBT_Inv(nbt: NBTTagCompound, key: String): Unit = {
		nbt.setTag(key, this.writeNBT_Inv())
	}

	def readNBT_Inv(nbt: NBTTagCompound, key: String): Unit = {
		this.readNBT_IInv(nbt.getCompoundTag(key))
	}

	private def writeNBT_IInv(tagCom: NBTTagCompound): Unit = {
		tagCom.setBoolean("has", this.hasInventory())
		if (this.hasInventory()) {
			tagCom.setInteger("size", this.getSizeInventory)

			val tagList: NBTTagList = new NBTTagList()
			for (slotID <- 0 until this.getSizeInventory) {
				if (this.getStackInSlot(slotID) != null) {
					val stackTagCom: NBTTagCompound = new NBTTagCompound()
					stackTagCom.setInteger("slot", slotID.asInstanceOf[Byte])
					this.getStackInSlot(slotID).writeToNBT(stackTagCom)
					tagList.appendTag(stackTagCom)
				}
			}
			tagCom.setTag("stacks", tagList)

			tagCom.setInteger("maxstacksize", this.stackSize)
		}
	}

	def readNBT_IInv(tagCom: NBTTagCompound): Unit = {
		if (tagCom.getBoolean("has")) {
			this.slots = new Array[ItemStack](tagCom.getInteger("size"))

			val tagList: NBTTagList = tagCom.getTagList("stacks", 10)
			for (tagIndex <- 0 to tagList.tagCount()) {
				val stackTagCom: NBTTagCompound = tagList.getCompoundTagAt(tagIndex)
				val slotID: Int = stackTagCom.getInteger("slot") & 255
				if (slotID >= 0 && slotID < this.getSizeInventory) {
					this.slots(slotID) = ItemStack.loadItemStackFromNBT(stackTagCom)
				}
			}

			this.stackSize = tagCom.getInteger("maxstacksize")
		}
		else {
			this.slots = new Array[ItemStack](0)
			this.stackSize = 64
		}
	}

	override def hasCustomInventoryName: Boolean = false

}

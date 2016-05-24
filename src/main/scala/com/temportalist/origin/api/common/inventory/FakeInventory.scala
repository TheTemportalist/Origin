package com.temportalist.origin.api.common.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

/**
 * A default fake inventory for easy access
 *
 * @param holderStack
 * The itemStack used to instantiate this inventory
 * @param inventorySize
 * The size of this inventory (number of slots)
 * @param stackLimit
 * The maximum stack size per slot
 *
 * @author TheTemportalist
 */
class FakeInventory(var holderStack: ItemStack, var inventorySize: Int, var stackLimit: Int)
		extends IInventory {

	// Default Constructor
	private val inv: Array[ItemStack] = new Array[ItemStack](this.inventorySize)
	// End Constructor

	def getSizeInventory: Int = {
		this.inventorySize
	}

	def getStackInSlot(slot: Int): ItemStack = {
		this.inv(slot)
	}

	def decrStackSize(slot: Int, amount: Int): ItemStack = {
		if (this.inv(slot) != null) {
			var itemstack: ItemStack = null
			if (this.inv(slot).stackSize <= amount) {
				itemstack = this.inv(slot)
				this.inv(slot) = null
				this.markDirty
				itemstack
			}
			else {
				itemstack = this.inv(slot).splitStack(amount)
				if (this.inv(slot).stackSize == 0) {
					this.inv(slot) = null
				}
				this.markDirty
				itemstack
			}
		}
		else {
			null
		}
	}

	def getStackInSlotOnClosing(slot: Int): ItemStack = {
		this.getStackInSlot(slot)
	}

	def setInventorySlotContents(slot: Int, stack: ItemStack) {
		this.inv(slot) = stack
		this.markDirty
	}

	override def closeInventory(): Unit = {}

	override def openInventory(): Unit = {}

	override def hasCustomInventoryName: Boolean = false

	override def getInventoryName: String =
		if (this.holderStack != null) this.holderStack.getDisplayName else ""

	/*
	override def getCommandSenderName: String = if (this.holderStack != null) this.holderStack.getDisplayName else ""

	override def hasCustomName: Boolean = false

	override def getDisplayName: IChatComponent =
		(if (this.hasCustomName)
			new ChatComponentText(this.getCommandSenderName)
		else
			new ChatComponentTranslation(this.getCommandSenderName, new Array[AnyRef](0))
		).asInstanceOf[IChatComponent]
	*/

	def getInventoryStackLimit: Int = {
		this.stackLimit
	}

	def markDirty() {
	}

	def isUseableByPlayer(var1: EntityPlayer): Boolean = {
		true
	}

	def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
		true
	}

}


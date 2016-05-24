package com.temportalist.origin.api.common.inventory

import com.temportalist.origin.api.common.container.{SlotValidate, SlotFinal, SlotGhost}
import com.temportalist.origin.api.common.utility.Stacks
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{Container, IInventory, Slot}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

import scala.util.control.Breaks._

/**
 *
 *
 * @author TheTemportalist
 */
class ContainerBase(var player: EntityPlayer, var inventory: IInventory) extends Container() {

	this.registerSlots()
	var needsUpdate = false

	private var countedSlots: Int = 0

	/**
	 * Used to register slots for this container
	 * Subclasses SHOULD use this method (that is the reason we have containers),
	 * however, subclasses do not NEED to use this method.
	 */
	protected def registerSlots(): Unit = {
	}

	protected def registerSlot(slotID: Int, slotX: Int, slotY: Int,
			isFinal: Boolean = false, doValidate: Boolean = false): Unit = {
		if (isFinal) {
			this.addSlotToContainer(new SlotFinal(this.inventory, slotID, slotX, slotY))
		}
		else if (doValidate) {
			this.addSlotToContainer(new SlotValidate(this.inventory, slotID, slotX, slotY))
		}
		else {
			this.addSlotToContainer(new Slot(this.inventory, slotID, slotX, slotY))
		}
		this.countedSlots += 1
	}

	/**
	 * Method to auto-generate slots connected to this player's inventory
	 *
	 * @param offsetX
	 * @param offsetY
	 */
	protected def registerPlayerSlots(offsetX: Int = 0, offsetY: Int = 0): Unit = {
		this.registerPlayerSlots(offsetX, offsetY, new Array[Int](0))
	}

	protected def registerPlayerSlots(offsetX: Int, offsetY: Int,
			finalSlotIDs: Array[Int]): Unit = {
		val slotSize: Int = 18
		val startX: Int = 12 + offsetX
		val startY: Int = 84 + offsetY
		for (col <- 0 until 9) {
			val x: Int = col * slotSize + startX
			var y: Int = startY
			if (finalSlotIDs.contains(col))
				this.addSlotToContainer(new SlotFinal(this.player.inventory, col, x, startY + 67))
			else this.addSlotToContainer(new Slot(this.player.inventory, col, x, startY + 67))
			for (row <- 0 until 3) {
				y = (startY + row * slotSize) + 9
				val id = (row + 1) * 9 + col
				//println("added slot with id " + id)
				if (finalSlotIDs.contains(id))
					this.addSlotToContainer(new SlotFinal(this.player.inventory, id, x, y))
				else this.addSlotToContainer(new Slot(this.player.inventory, id, x, y))

			}
		}
	}

	// ~~~~~~~~~~~~~~~~~~~~ Get Inventory !~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Get the invenotry to which this container belongs
	 *
	 * @return
	 */
	def getIInventory: IInventory = {
		this.inventory
	}

	/**
	 * Discern whether or not the inventory that this container belongs to is a tile entity
	 *
	 * @return
	 */
	def isAttachedToTileEntity: Boolean = {
		this.inventory.isInstanceOf[TileEntity]
	}

	/**
	 * Get the tile entity to which this container belongs
	 *
	 * @return
	 */
	def getTileEntity: TileEntity = {
		if (this.isAttachedToTileEntity) {
			return this.inventory.asInstanceOf[TileEntity]
		}
		null
	}

	/**
	 * Discern whether or not the inventory that this container belongs to is an Item
	 *
	 * @return
	 */
	def isAttachedToItem: Boolean = {
		this.inventory.isInstanceOf[InventoryWrapper] &&
				this.inventory.asInstanceOf[InventoryWrapper].isItemInventory
	}

	/**
	 * Get the item inventory to which this container belongs
	 *
	 * @return
	 */
	def getItemInventory: InventoryWrapper = {
		if (this.isAttachedToItem) {
			return this.inventory.asInstanceOf[InventoryWrapper]
		}
		null
	}

	// ~~~~~~~~~~~~~~~~~~~~ Interactions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Discern whether or not the passed player can use this container.
	 */
	override def canInteractWith(player: EntityPlayer): Boolean = {
		this.inventory.isUseableByPlayer(player)
	}

	/**
	 * Used to move item stacks about when a player shiftclicks
	 */
	///*
	override def transferStackInSlot(player: EntityPlayer, slotID: Int): ItemStack = {
		val slot: Slot = this.inventorySlots.get(slotID).asInstanceOf[Slot]
		var stackInSlot: ItemStack = null
		if (slot != null && slot.getHasStack) {
			val slotStack: ItemStack = slot.getStack
			stackInSlot = slotStack.copy()

			val invSize: Int = this.getInventorySlotSize
			val maxSlots: Int = this.inventorySlots.size()

			if (slotID < invSize) {
				if (!this.mergeItemStack(slotStack, invSize, maxSlots, isBackwards = true))
					return null
			}
			else if (!this.mergeItemStack(slotStack, 0, invSize, isBackwards = false))
				return null
			if (slotStack.stackSize == 0)
				slot.putStack(null)
			else slot.onSlotChanged()
		}
		stackInSlot
	}

	/**
	 * @return the number of slots that are present and connected to this inventory
	 */
	protected def getInventorySlotSize: Int = 0 //this.countedSlots

	override def mergeItemStack(stack: ItemStack, minSlotID: Int, maxSlotID: Int,
			isBackwards: Boolean): Boolean = {
		var retStack: Boolean = false
		var slotID: Int = if (isBackwards) maxSlotID - 1 else minSlotID
		var slot: Slot = null
		var local: ItemStack = null
		if (stack.isStackable) {
			while (stack.stackSize > 0 && (
					(!isBackwards && slotID < maxSlotID) ||
							(isBackwards && slotID >= minSlotID)
					)) {
				slot = this.inventorySlots.get(slotID).asInstanceOf[Slot]
				local = slot.getStack
				if (slot.isItemValid(stack) && Stacks.doStacksMatch(stack, local,
					meta = true, size = false, nbt = true, nil = false
				)) {
					val k: Int = local.stackSize + stack.stackSize
					val m: Int = Math.min(stack.getMaxStackSize, slot.getSlotStackLimit)
					if (k <= m) {
						stack.stackSize = 0
						local.stackSize = k
						slot.onSlotChanged()
						retStack = true
					}
					else if (local.stackSize < m) {
						stack.stackSize -= m - local.stackSize
						local.stackSize = m
						slot.onSlotChanged()
						retStack = true
					}
				}
				slotID += (if (isBackwards) -1 else 1)
			}
		}
		if (stack.stackSize > 0) {
			slotID = if (isBackwards) maxSlotID - 1 else minSlotID

			breakable {
				while ((!isBackwards && slotID < maxSlotID) ||
						(isBackwards && slotID >= minSlotID)) {
					slot = this.inventorySlots.get(slotID).asInstanceOf[Slot]
					local = slot.getStack
					if (slot.isItemValid(stack) && local == null) {
						val nextStack: ItemStack = stack.copy()
						nextStack.stackSize = Math.min(stack.stackSize, slot.getSlotStackLimit)
						slot.putStack(nextStack)
						slot.onSlotChanged()
						if (slot.getStack != null) {
							stack.stackSize -= slot.getStack.stackSize
							retStack = true
						}
						break()
					}
					slotID += (if (isBackwards) -1 else 1)
				}
			}
		}

		retStack
	}

	protected def getSlotIDForItemStack(stackToProcess: ItemStack): Int = {
		-1
	}

	protected def getExcludedMaximumSlotIDForItemStack(stackToProcess: ItemStack): Int = {
		this.getIInventory.getSizeInventory
	}

	protected def isItemValidForSlotOnShift(slot: Slot, stackToProcess: ItemStack): Boolean = {
		if (slot.isInstanceOf[SlotGhost]) {
			false
		}
		else {
			slot.isItemValid(stackToProcess)
		}
	}

	override def slotClick(slotID: Int, mouseButton: Int, flag: Int,
			player: EntityPlayer): ItemStack = {
		if (this.isAttachedToItem) {
			this.needsUpdate = true
		}
		if (slotID >= 0 && slotID < this.inventorySlots.size() &&
				this.inventorySlots.get(slotID).isInstanceOf[SlotGhost]) {
			return this.inventorySlots.get(slotID).asInstanceOf[SlotGhost]
					.ghostSlotClick(mouseButton, player)
		}
		super.slotClick(slotID, mouseButton, flag, player)
	}

	// ~~~~~~~~~~~~~~~~~~~~ Item NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	def writeToNBT(): Unit = {
		if (this.isAttachedToItem) {
			val itemStack: ItemStack = this.player.getHeldItem
			if (itemStack != null) {
				val tagCom: NBTTagCompound = itemStack.getTagCompound
				val inventoryTagCom: NBTTagCompound = new NBTTagCompound()
				this.inventory.asInstanceOf[InventoryWrapper].writeToNBT(inventoryTagCom)
				tagCom.setTag("InventoryData", inventoryTagCom)
				itemStack.setTagCompound(tagCom)
			}
		}
	}

}

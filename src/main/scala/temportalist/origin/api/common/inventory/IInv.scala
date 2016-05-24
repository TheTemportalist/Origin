package temportalist.origin.api.common.inventory

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.{EnumFacing, ChatComponentTranslation, ChatComponentText, IChatComponent}

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

	def hasInventory: Boolean = this.slots != null

	//this.hasInventory() && MathFuncs.between(0, index, this.getSizeInventory)
	def isValidSlot(index: Int): Boolean = true

	override def getStackInSlot(index: Int): ItemStack = {
		if (this.isValidSlot(index)) {
			this.slots(index)
		}
		else null
	}

	override def removeStackFromSlot(index: Int): ItemStack = {
		if (this.slots(index) != null) {
			val ret = this.slots(index)
			this.slots(index) = null
			ret
		} else null
	}

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

	override def openInventory(player: EntityPlayer): Unit = {}

	override def closeInventory(player: EntityPlayer): Unit = {}

	override def hasCustomName: Boolean = false

	override def getName: String = ""

	override def getDisplayName: IChatComponent = {
		if (this.hasCustomName)
			new ChatComponentText(this.getName)
		else
			new ChatComponentTranslation(this.getName, new Array[AnyRef](0))
	}

	override def markDirty(): Unit = {}

	override def setField(id: Int, value: Int): Unit = {}

	override def getField(id: Int): Int = 0

	override def getFieldCount: Int = 0

	override def isUseableByPlayer(playerIn: EntityPlayer): Boolean = true

	override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = this.isValidSlot(index)



	override def getSlotsForFace(side: EnumFacing): Array[Int] = {
		if (this.hasInventory) {
			val slotsFromSide: Array[Int] = new Array[Int](this.getSizeInventory)
			for (i <- 0 until this.getSizeInventory) {
				slotsFromSide(i) = i
			}
			slotsFromSide
		}
		else null
	}

	override def canExtractItem(index: Int, stack: ItemStack,
			direction: EnumFacing): Boolean = this.isValidSlot(index)

	override def canInsertItem(index: Int, itemStackIn: ItemStack,
			direction: EnumFacing): Boolean = this.isValidSlot(index)

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
		tagCom.setBoolean("has", this.hasInventory)
		if (this.hasInventory) {
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

}

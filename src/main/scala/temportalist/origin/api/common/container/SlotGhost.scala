package temportalist.origin.api.common.container

import net.minecraft.entity.player.{EntityPlayer, InventoryPlayer}
import net.minecraft.inventory.{IInventory, Slot}
import net.minecraft.item.ItemStack
;

/**
 * A slot which holds a ghost version of whichever item is put into it
 *
 * @author TheTemportalist
 */
class SlotGhost(inv: IInventory, slotID: Int, x: Int, y: Int) extends Slot(inv, slotID, x, y) {

	var maxStackSize: Int = -1

	def this(inv: IInventory, slotID: Int, x: Int, y: Int, maxSize: Int) {
		this(inv, slotID, x, y)
		this.maxStackSize = maxSize

	}

	override def canTakeStack(par1EntityPlayer: EntityPlayer): Boolean = {
		// Make sure you can never extract from a ghost slot
		false
	}

	override def getSlotStackLimit: Int = {
		if (this.maxStackSize > 0) this.maxStackSize
		else this.inventory.getInventoryStackLimit
	}

	def ghostSlotClick(mouseButton: Int, player: EntityPlayer): ItemStack = {
		// left or right mouse buttons
		if (mouseButton == 0 || mouseButton == 1) {
			// get the inventory of the player
			val invPlayer: InventoryPlayer = player.inventory
			// get the stack in this slot
			val stackInSlot: ItemStack = this.getStack
			// get the player's held stack
			val heldStack: ItemStack = invPlayer.getItemStack
			// if this stack is not null
			if (stackInSlot == null) {
				// if player's stack is valid
				if (heldStack != null && this.isItemValid(heldStack)) {
					// recalculate the stack's size
					var stackSize: Int = if (mouseButton == 0) heldStack.stackSize else 1
					// match with required bounds
					if (stackSize > this.getSlotStackLimit) {
						stackSize = this.getSlotStackLimit
					}
					// the new stack
					val ghostStack: ItemStack = heldStack.copy
					// set the new stack's stack size
					ghostStack.stackSize = stackSize
					// insert new stack to self
					this.putStack(ghostStack)

				}
			}
			else {
				// self's stack is not empty, so make it so
				this.putStack(null)
			}
		}
		// return self's stack
		this.getStack
	}

}

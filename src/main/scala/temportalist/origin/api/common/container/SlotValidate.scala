package temportalist.origin.api.common.container

import net.minecraft.inventory.{IInventory, Slot}
import net.minecraft.item.ItemStack

/**
 *
 *
 * @author TheTemportalist
 */
class SlotValidate(inv: IInventory, index: Int, x: Int, y: Int) extends Slot(inv, index, x, y) {

	override def isItemValid(stack: ItemStack): Boolean =
		this.inventory.isItemValidForSlot(this.getSlotIndex, stack)
	
}

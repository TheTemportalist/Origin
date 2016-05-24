package temportalist.origin.api.common.container

import net.minecraft.inventory.{IInventory, Slot}
import net.minecraft.item.ItemStack

/**
 * A final slot of only outputs
 *
 * @author TheTemportalist
 */
class SlotOutput(inv: IInventory, id: Int, x: Int, y: Int) extends Slot(inv, id, x, y) {

	override def isItemValid(par1ItemStack: ItemStack): Boolean = {
		false
	}

}

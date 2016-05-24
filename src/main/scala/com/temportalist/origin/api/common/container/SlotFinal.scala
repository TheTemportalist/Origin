package com.temportalist.origin.api.common.container

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

/**
 * A slot you cannot interfere with
 *
 * @author TheTemportalist
 */
class SlotFinal(inventory: IInventory, slotID: Int, x: Int, y: Int)
		extends SlotOutput(inventory, slotID, x, y) {

	override def isItemValid(itemStack: ItemStack): Boolean = {
		false
	}

	override def canTakeStack(player: EntityPlayer): Boolean = {
		false
	}

}

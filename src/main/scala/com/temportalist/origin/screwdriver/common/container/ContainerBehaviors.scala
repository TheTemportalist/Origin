package com.temportalist.origin.screwdriver.common.container

import com.temportalist.origin.api.common.inventory.ContainerBase
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

/**
 * Created by TheTemportalist on 12/21/2015.
 */
class ContainerBehaviors(p: EntityPlayer)
		extends ContainerBase(p, new InvScrewdriver(p, p.getCurrentEquippedItem)) {

	/**
	 * Used to register slots for this container
	 * Subclasses SHOULD use this method (that is the reason we have containers),
	 * however, subclasses do not NEED to use this method.
	 */
	override protected def registerSlots(): Unit = {
		val stack = p.getCurrentEquippedItem
		for (i <- 0 until AddonScrewdriver.NBTBehaviorHelper.getInventorySize(stack)) {
			val col = i % 3
			val row = i / 3
			val x = col * 18 + 8
			val y = (row * 18) + 5
			this.registerSlot(i, x, y, doValidate = true)
		}
		this.registerPlayerSlots(-4, -10, Array[Int](this.player.inventory.currentItem))
	}

	override def onContainerClosed(player: EntityPlayer): Unit = {
		super.onContainerClosed(player)
		this.inventory.markDirty()
	}

	override def slotClick(slotID: Int, mouseButton: Int, flag: Int,
			player: EntityPlayer): ItemStack = {
		val ret = super.slotClick(slotID, mouseButton, flag, player)
		this.inventory.asInstanceOf[InvScrewdriver].writeModules()
		ret
	}

}

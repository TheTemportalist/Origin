package temportalist.origin.screwdriver.common.container

import com.temportalist.origin.api.common.inventory.IInv
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import com.temportalist.origin.screwdriver.common.network.PacketUpdateItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}

/**
 * Created by TheTemportalist on 12/21/2015.
 */
class InvScrewdriver(player: EntityPlayer, private var stack: ItemStack) extends IInv {

	this.setSlots(12, 1)
	this.readModules()

	override def markDirty(): Unit = {
		//this.writeModules()
	}

	override def getInventoryName: String = ""

	def readModules(): Unit = {
		val modules = AddonScrewdriver.NBTBehaviorHelper.getModules(this.stack)
		for (i <- modules.indices) this.setInventorySlotContents(i, modules(i))
	}

	def writeModules(): Unit = {
		AddonScrewdriver.NBTBehaviorHelper.updateModuleBehaviors(stack, this.slots.clone())
		AddonScrewdriver.NBTBehaviorHelper.writeModules(stack, this.slots.clone())
		new PacketUpdateItem(this.stack).sendToServer(AddonScrewdriver)
	}

	def updateInventoryStacks(): Unit = {
		this.stack = this.player.getCurrentEquippedItem
	}

	override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = {
		super.isItemValidForSlot(index, stack) &&
			AddonScrewdriver.getBehaviorIDsProvided(stack).nonEmpty
	}

}

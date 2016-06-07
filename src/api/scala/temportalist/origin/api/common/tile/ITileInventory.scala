package temportalist.origin.api.common.tile

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.{CapabilityItemHandler, ItemStackHandler}

/**
  *
  * Created by TheTemportalist on 6/7/2016.
  *
  * @author TheTemportalist
  */
trait ITileInventory extends TileEntity {

	private val inventory = new ItemStackHandler(this.getSlots)

	def getSlots: Int

	final def getInventory: ItemStackHandler = this.inventory

	final def serializeInventory: NBTTagCompound = {
		this.inventory.serializeNBT()
	}

	final def deserializeInventory(nbt: NBTTagCompound): Unit ={
		this.inventory.deserializeNBT(nbt)
	}

	override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) true
		else super.hasCapability(capability, facing)
	}

	override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory)
		else super.getCapability(capability, facing)
	}

}

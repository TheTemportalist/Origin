package temportalist.origin.foundation.common.tile

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import temportalist.origin.api.common.inventory.IInv
import temportalist.origin.api.common.tile.{ITileSaver, IPowerable, ITank}

/**
 * A wrapper class for Minecraft's TileEntity
 *
 * @param name
 * The name of this tile entity, this is superficial, used normally only for inventories
 *
 * @author TheTemportalist
 */
class TEBase(var name: String) extends TileEntity() with IInv with ITank with IPowerable with ITileSaver {

	def this() {
		this("")
	}

	override def getName: String = this.name

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Write/Read NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def updateTile(): Unit = this.markForUpdate()

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		super.writeToNBT(tagCom)

		tagCom.setString("TEWrapper_teName", this.name)

		this.writeNBT_Inv(tagCom, "inventory")

		val tanksTag: NBTTagCompound = new NBTTagCompound
		this.writeNBT_ITank(tanksTag)
		tagCom.setTag("tanks", tanksTag)

	}

	override def readFromNBT(tagCom: NBTTagCompound): Unit = {
		super.readFromNBT(tagCom)

		this.name = tagCom.getString("TEWrapper_teName")

		this.readNBT_Inv(tagCom, "inventory")

		this.fromNBT_ITank(tagCom.getCompoundTag("tanks"))

	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Other ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def markDirty(): Unit = {
		this.markForUpdate()
	}

	def markForUpdate(): Unit = {
		this.worldObj.markBlockForUpdate(this.getPos)
	}

}

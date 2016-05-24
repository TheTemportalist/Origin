package com.temportalist.origin.foundation.common.tile

import com.temportalist.origin.api.common.inventory.IInv
import com.temportalist.origin.api.common.lib.{BlockCoord, V3O}
import com.temportalist.origin.api.common.tile.{IPowerable, ITank, ITileSaver}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

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

	override def getInventoryName: String = this.name

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Write/Read NBT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def updateTile(): Unit = this.markforUpdate()

	override def writeToNBT(tagCom: NBTTagCompound): Unit = {
		super.writeToNBT(tagCom)

		tagCom.setString("TEWrapper_teName", this.name)

		this.writeNBT_Inv(tagCom, "inventory")

		val tanksTag: NBTTagCompound = new NBTTagCompound
		this.toNBT_ITank(tanksTag)
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

		val thisCoord: BlockCoord = new BlockCoord(this)
		thisCoord.scheduleUpdate()
		thisCoord.markForUpdate()

	}

	def markforUpdate(): Unit = {
		new V3O(this).markForUpdate(this.getWorldObj)
	}

}

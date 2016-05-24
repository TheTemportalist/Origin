package com.temportalist.origin.api.common.tile

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.network.{NetworkManager, Packet}
import net.minecraft.tileentity.TileEntity

/**
 *
 *
 * @author TheTemportalist
 */
trait ITileSaver extends TileEntity {

	override def getDescriptionPacket: Packet = {
		val tagCom: NBTTagCompound = new NBTTagCompound
		this.writeToNBT(tagCom)
		new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, this.getBlockMetadata, tagCom)
	}

	override def onDataPacket(net: NetworkManager, pkt: S35PacketUpdateTileEntity) {
		this.readFromNBT(pkt.func_148857_g())
	}

}

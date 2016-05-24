package temportalist.origin.api.common.tile

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.network.{INetHandler, NetworkManager, Packet}
import net.minecraft.tileentity.TileEntity

/**
 *
 *
 * @author TheTemportalist
 */
trait ITileSaver extends TileEntity {

	override def getDescriptionPacket: Packet[_ <: INetHandler] = {
		val tagCom: NBTTagCompound = new NBTTagCompound
		this.writeToNBT(tagCom)
		new S35PacketUpdateTileEntity(this.getPos, this.getBlockMetadata, tagCom)
	}

	override def onDataPacket(net: NetworkManager, pkt: S35PacketUpdateTileEntity) {
		this.readFromNBT(pkt.getNbtCompound)
	}

}

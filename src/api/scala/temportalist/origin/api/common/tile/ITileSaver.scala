package temportalist.origin.api.common.tile

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity

/**
  * A simple tile entity interface which implements data packets for tile syncing
  *
  * Created by TheTemportalist on 4/15/2016.
  * @author TheTemportalist
  */
trait ITileSaver extends TileEntity {

	override def getUpdatePacket: SPacketUpdateTileEntity = {
		val tagCom: NBTTagCompound = new NBTTagCompound
		this.writeToNBT(tagCom)
		new SPacketUpdateTileEntity(this.getPos, this.getBlockMetadata, tagCom)
	}

	override def onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
		this.readFromNBT(pkt.getNbtCompound)
	}

}

package temportalist.origin.foundation.common.capability

import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint
import temportalist.origin.foundation.common.network.{NetworkMod, PacketExtendedSync}

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
trait IExtendedEntitySync[N <: NBTTagCompound, E <: Entity] extends INBTSerializable[N] {

	final def markDirtyAll(entity: E): Unit = this.markDirty(entity, EnumDirty.ALL, null, null)

	final def markDirtySpecific(entity: E, getNBT: (Any*) => NBTTagCompound, data: Any*): Unit =
		this.markDirty(entity, EnumDirty.SPECIFIC, getNBT, data:_*)

	final def markDirty(entity: E, state: EnumDirty, getNBT: (Any*) => NBTTagCompound, data: Any*): Unit = {
		state match {
			case EnumDirty.ALL => this.sendNBTToClient(entity, this.serializeNBT)
			case EnumDirty.SPECIFIC => this.sendNBTToClient(entity, getNBT(data:_*))
		}
	}

	def getPacketTargetPoint(e: E): TargetPoint = {
		new TargetPoint(e.dimension, e.posX, e.posY, e.posZ, 128)
	}

	def getNetworkMod: NetworkMod

	final def constructPacket(entity: E, tag: NBTTagCompound): PacketExtendedSync = {
		new PacketExtendedSync(entity.getEntityId, tag)
	}

	def sendNBTToClient(entity: E, nbt: NBTTagCompound): Unit = {
		this.constructPacket(entity, nbt).sendToDimension(
			this.getNetworkMod, entity.getEntityWorld.provider.getDimension)
	}

	def sendNBTToServer(entity: E, nbt: NBTTagCompound): Unit = {
		this.constructPacket(entity, nbt).sendToServer(this.getNetworkMod)
	}

}

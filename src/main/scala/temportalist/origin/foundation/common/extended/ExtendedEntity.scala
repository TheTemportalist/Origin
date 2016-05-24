package temportalist.origin.foundation.common.extended

import net.minecraft.entity.Entity
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.IExtendedEntityProperties
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.foundation.common.network.PacketExtendedSync
import temportalist.origin.internal.common.Origin
import temportalist.origin.internal.common.extended.ExtendedEntityHandler

/**
 *
 *
 * @author TheTemportalist
 */
abstract class ExtendedEntity(private var entity: EntityPlayer)
		extends IExtendedEntityProperties {

	override def init(entity: Entity, world: World): Unit = {}

	def getEntity: EntityPlayer = this.entity

	final def saveNBTData(tagCom: NBTTagCompound): Unit = {
		val extTag = new NBTTagCompound
		this.writeNBT(extTag)
		tagCom.setTag(ExtendedEntityHandler.getClassKey(this.getClass), extTag)
	}

	def writeNBT(tagCom: NBTTagCompound): Unit

	final def loadNBTData(tagCom: NBTTagCompound): Unit = {
		this.readNBT(tagCom.getCompoundTag(ExtendedEntityHandler.getClassKey(this.getClass)))
	}

	def readNBT(tagCom: NBTTagCompound): Unit

	def syncEntity(id: String, data: Any*): Unit = {
		if (this.isClient)
			throw new IllegalStateException("Cannot sync entity from client side to server side.")
		val packet = new PacketExtendedSync(this.getClass, id)
		data.foreach(any => {
			try {
				packet.addAny(any)
			}
			catch {
				case e: Exception =>
					throw new IllegalArgumentException("Could not find a way to handle adding "
							+ any + " to PacketExtendedSync")
			}
		})
		packet.sendToPlayer(Origin, this.getEntity.asInstanceOf[EntityPlayerMP])
	}

	def addDataToPacket(packet: PacketExtendedSync, data: Any): Boolean = false

	def handleSyncPacketData(uniqueIdentifier: String, packet: PacketExtendedSync,
			side: Side): Unit = {
		Origin.log("Error: If packets are being sent to " + this.getClass.getCanonicalName +
				", then said class must override ExtendedEntity.handleSyncPacketData")
	}

	final def isClient: Boolean = this.getEntity.getEntityWorld.isRemote

	final def isClientCheck: Boolean = {
		if (this.isClient) {
			Origin.log("WARNING: Performing server-side action on client-side.")
			true
		} else false
	}
}

package com.temportalist.origin.foundation.common.extended

import com.temportalist.origin.api.common.utility.WorldHelper
import com.temportalist.origin.foundation.common.network.PacketExtendedSync
import com.temportalist.origin.internal.common.Origin
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.IExtendedEntityProperties

/**
 *
 *
 * @author TheTemportalist
 */
abstract class ExtendedEntity(private var entity: EntityPlayer)
		extends IExtendedEntityProperties {

	override def init(entity: Entity, world: World): Unit = {}

	def getEntity: EntityPlayer = this.entity

	def saveNBTData(tagCom: NBTTagCompound): Unit

	def loadNBTData(tagCom: NBTTagCompound): Unit

	def syncEntity(id: String, data: Any*): Unit = {
		this.syncEntityFrom(WorldHelper.getSide, id, data.seq)
	}

	def syncEntityFrom(side: Side, id: String, data: Seq[Any]): Unit = {
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
		packet.sendToOpposite(Origin, side, this.entity)
	}

	def syncEntityFull(): Unit = {
		val tagCom = new NBTTagCompound
		this.saveNBTData(tagCom)
		new PacketExtendedSync(this.getClass, "").add(tagCom).
				sendToOpposite(Origin, WorldHelper.getSide, this.entity)
	}

	def addDataToPacket(packet: PacketExtendedSync, data: Any): Boolean = false

	def handleSyncPacketData(uniqueIdentifier: String, packet: PacketExtendedSync,
			side: Side): Unit = {
		Origin.log("Error: If packets are being sent to " + this.getClass.getCanonicalName +
				", then said class must override ExtendedEntity.handleSyncPacketData")
	}

}

package com.temportalist.origin.foundation.common.network

import com.temportalist.origin.foundation.common.extended.ExtendedEntity
import com.temportalist.origin.internal.common.extended.ExtendedEntityHandler
import cpw.mods.fml.common.network.simpleimpl.{MessageContext, IMessage, IMessageHandler}
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

/**
 *
 *
 * @author  TheTemportalist  5/21/15
 */
class PacketExtendedSync extends IPacket {

	def this(extendedClass: Class[_ <: ExtendedEntity], uniqueID: String) {
		this()
		this.add(ExtendedEntityHandler.getClassKey(extendedClass))
		this.add(uniqueID)
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketExtendedSync {
	class Handler extends IMessageHandler[PacketExtendedSync, IMessage] {
		override def onMessage(message: PacketExtendedSync, ctx: MessageContext): IMessage = {
			val extended = ExtendedEntityHandler.getExtendedByKey(
				ctx.getServerHandler.playerEntity, message.get[String])
			val id = message.get[String]
			if (!id.isEmpty) extended.handleSyncPacketData(id, message, ctx.side)
			else extended.loadNBTData(message.get[NBTTagCompound])
			null
		}
	}
}

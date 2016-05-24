package temportalist.origin.foundation.common.network

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.network.simpleimpl.{MessageContext, IMessage, IMessageHandler}
import net.minecraftforge.fml.relauncher.{SideOnly, Side}
import temportalist.origin.foundation.common.extended.ExtendedEntity
import temportalist.origin.internal.common.extended.ExtendedEntityHandler

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

	override def getReceivableSide: Side = Side.CLIENT

}
object PacketExtendedSync {
	class Handler extends IMessageHandler[PacketExtendedSync, IMessage] {
		override def onMessage(message: PacketExtendedSync, ctx: MessageContext): IMessage = {
			val extended = ExtendedEntityHandler.getExtendedByKey(getPlayer, message.get[String])
			if (extended != null) {
				val id = message.get[String]
				if (!id.isEmpty) extended.handleSyncPacketData(id, message, ctx.side)
				else extended.loadNBTData(message.get[NBTTagCompound])
			}
			null
		}
	}
	@SideOnly(Side.CLIENT)
	def getPlayer: EntityPlayer = {
		net.minecraft.client.Minecraft.getMinecraft.thePlayer
	}
}

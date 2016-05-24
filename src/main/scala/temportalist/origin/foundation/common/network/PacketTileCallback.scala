package temportalist.origin.foundation.common.network

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.foundation.common.tile.IPacketCallback

/**
 *
 *
 * @author TheTemportalist
 */
class PacketTileCallback extends IPacket {

	def this(tile: TileEntity) {
		this()
		this.add(tile)
	}

	override def getReceivableSide: Side = null

}
object PacketTileCallback {
	class Handler extends IMessageHandler[PacketTileCallback, IMessage] {
		override def onMessage(message: PacketTileCallback, ctx: MessageContext): IMessage = {
			message.getTile(ctx.getServerHandler.playerEntity.worldObj) match {
				case callback: IPacketCallback =>
					callback.packetCallback(message, ctx.side)
				case _ =>
			}
			null
		}
	}
}

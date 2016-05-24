package com.temportalist.origin.foundation.common.network

import com.temportalist.origin.foundation.common.tile.IPacketCallback
import cpw.mods.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import cpw.mods.fml.relauncher.Side
import net.minecraft.tileentity.TileEntity

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

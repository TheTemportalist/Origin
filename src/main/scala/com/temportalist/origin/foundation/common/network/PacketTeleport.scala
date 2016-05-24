package com.temportalist.origin.foundation.common.network

import com.temportalist.origin.api.common.utility.Teleport
import cpw.mods.fml.common.network.simpleimpl.{MessageContext, IMessage, IMessageHandler}
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}

/**
 *
 *
 * @author TheTemportalist
 */
class PacketTeleport extends IPacket {

	def this(dimId: Int, coords: Array[Double], fall: Boolean, particles: Boolean) {
		this()
		this.add(dimId)
		this.add(coords)
		this.add(fall)
		this.add(particles)
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketTeleport {
	class Handler extends IMessageHandler[PacketTeleport, IMessage] {
		override def onMessage(message: PacketTeleport, ctx: MessageContext): IMessage = {
			val player = ctx.getServerHandler.playerEntity
			Teleport.toDimension(player, message.get[Int])
			val coord: Array[Double] = message.get[Array[Double]]
			val fall: Boolean = message.get[Boolean]
			val particles: Boolean = message.get[Boolean]
			Teleport.toPoint(player.asInstanceOf[EntityPlayerMP], coord(0), coord(1), coord(2))
			null
		}
	}
}

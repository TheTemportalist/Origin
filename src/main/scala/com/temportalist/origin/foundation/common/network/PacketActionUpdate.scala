package com.temportalist.origin.foundation.common.network

import com.temportalist.origin.api.common.lib.BlockCoord
import com.temportalist.origin.api.common.tile.{ActivatedAction, IAction}
import cpw.mods.fml.common.network.simpleimpl.{MessageContext, IMessage, IMessageHandler}
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity

/**
 *
 *
 * @author TheTemportalist
 */
class PacketActionUpdate extends IPacket {

	def this(tile: TileEntity, state: ActivatedAction) {
		this()
		this.add(tile)
		if (state != null)
			this.add(state.ordinal())
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketActionUpdate {
	class Handler extends IMessageHandler[PacketActionUpdate, IMessage] {
		override def onMessage(message: PacketActionUpdate, ctx: MessageContext): IMessage = {
			message.getTile(ctx.getServerHandler.playerEntity.worldObj) match {
				case tileEntity: IAction =>
					tileEntity.setAction(ActivatedAction.values()(message.get[Int]))
					new BlockCoord(tileEntity).notifyAllOfStateChange()
				case _ =>
			}
			null
		}
	}
}

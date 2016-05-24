package com.temportalist.origin.foundation.common.network

import com.temportalist.origin.api.common.lib.BlockCoord
import com.temportalist.origin.api.common.tile.{IAction, ActivatedAction, IPowerable, RedstoneState}
import com.temportalist.origin.api.common.utility.Teleport
import cpw.mods.fml.common.network.simpleimpl.{MessageContext, IMessage, IMessageHandler}
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.{EntityPlayerMP, EntityPlayer}
import net.minecraft.tileentity.TileEntity

/**
 *
 *
 * @author TheTemportalist
 */
class PacketRedstoneUpdate extends IPacket {

	def this(tile: TileEntity, state: RedstoneState) {
		this()
		this.add(tile)
		if (state != null)
			this.add(state.ordinal())
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketRedstoneUpdate {
	class Handler extends IMessageHandler[PacketRedstoneUpdate, IMessage] {
		override def onMessage(message: PacketRedstoneUpdate, ctx: MessageContext): IMessage = {
			message.getTile(ctx.getServerHandler.playerEntity.worldObj) match {
				case tileEntity: IPowerable =>
					tileEntity.setRedstoneState(RedstoneState.values()(message.get[Int]))
					new BlockCoord(tileEntity).notifyAllOfStateChange()
				case _ =>
			}
			null
		}
	}
}

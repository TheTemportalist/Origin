package temportalist.origin.foundation.common.network

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.api.common.lib.V3O
import temportalist.origin.api.common.tile.{IAction, ActivatedAction}

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
					new V3O(tileEntity).notifyAllOfStateChange(tileEntity.getWorld)
				case _ =>
			}
			null
		}
	}
}

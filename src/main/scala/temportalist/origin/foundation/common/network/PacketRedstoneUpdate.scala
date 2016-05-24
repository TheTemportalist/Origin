package temportalist.origin.foundation.common.network

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.network.simpleimpl.{MessageContext, IMessage, IMessageHandler}
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.api.common.lib.V3O
import temportalist.origin.api.common.tile.{IPowerable, RedstoneState}

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
					new V3O(tileEntity).notifyAllOfStateChange(tileEntity.getWorld)
				case _ =>
			}
			null
		}
	}
}

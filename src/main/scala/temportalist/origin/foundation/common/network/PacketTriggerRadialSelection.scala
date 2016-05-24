package temportalist.origin.foundation.common.network

import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.api.common.lib.IRadialSelection
import temportalist.origin.foundation.client.gui.GuiRadialMenu

/**
 *
 *
 * @author TheTemportalist
 */
class PacketTriggerRadialSelection extends IPacket {

	def this(handlerID: Int, behavior: IRadialSelection) {
		this()
		this.add(handlerID)
		this.add(behavior.getGlobalID)
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketTriggerRadialSelection {
	class Handler extends IMessageHandler[PacketTriggerRadialSelection, IMessage] {
		override def onMessage(message: PacketTriggerRadialSelection,
				ctx: MessageContext): IMessage = {
			GuiRadialMenu.onSelection(message.get[Int], message.get[Int],
				ctx.getServerHandler.playerEntity)
			null
		}
	}
}

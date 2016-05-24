package com.temportalist.origin.foundation.common.network

import com.temportalist.origin.api.common.lib.IRadialSelection
import com.temportalist.origin.foundation.client.gui.GuiRadialMenu
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import cpw.mods.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import cpw.mods.fml.relauncher.Side

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

package com.temportalist.origin.screwdriver.common.network

import com.temportalist.origin.foundation.common.network.IPacket
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import com.temportalist.origin.screwdriver.common.container.{InvScrewdriver, ContainerBehaviors}
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.network.simpleimpl.{MessageContext, IMessage, IMessageHandler}
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.{EntityPlayerMP, EntityPlayer}
import net.minecraft.item.ItemStack

/**
 * Created by TheTemportalist on 12/22/2015.
 */
class PacketUpdateItem extends IPacket {

	def this(stack: ItemStack) {
		this()
		this.add(stack)
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketUpdateItem {
	class Handler extends IMessageHandler[PacketUpdateItem, IMessage] {
		override def onMessage(message: PacketUpdateItem, ctx: MessageContext): IMessage = {
			PacketUpdateItem.setStackAtCurrent(
				ctx.getServerHandler.playerEntity, message.get[ItemStack])
			ctx.getServerHandler.playerEntity.openContainer match {
				case contBehavior: ContainerBehaviors =>
					contBehavior.inventory.asInstanceOf[InvScrewdriver].updateInventoryStacks()
				case _ =>
			}
			null
		}
	}
	def setStackAtCurrent(player: EntityPlayer, stack: ItemStack): Unit = {
		player.inventory.setInventorySlotContents(
			player.inventory.currentItem, stack)
	}
}

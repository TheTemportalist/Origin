package temportalist.origin.screwdriver.common.network

import com.temportalist.origin.foundation.common.network.IPacket
import com.temportalist.origin.internal.common.Origin
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import cpw.mods.fml.common.network.simpleimpl.{MessageContext, IMessage, IMessageHandler}
import cpw.mods.fml.relauncher.{SideOnly, Side}
import net.minecraft.entity.player.EntityPlayer

/**
 * Created by TheTemportalist on 12/21/2015.
 */
class PacketOpenGui extends IPacket {

	def this(guiID: Int) {
		this()
		this.add(guiID)
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketOpenGui {
	class Handler extends IMessageHandler[PacketOpenGui, IMessage] {
		override def onMessage(message: PacketOpenGui, ctx: MessageContext): IMessage = {
			val player = ctx.getServerHandler.playerEntity
			player.openGui(AddonScrewdriver, message.get[Int], player.worldObj,
				player.posX.toInt, player.posY.toInt, player.posZ.toInt)
			null
		}

	}
}

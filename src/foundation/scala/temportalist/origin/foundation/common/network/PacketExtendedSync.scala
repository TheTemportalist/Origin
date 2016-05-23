package temportalist.origin.foundation.common.network
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
class PacketExtendedSync extends IPacket {

	def this(entityID: Int, nbt: NBTTagCompound) {
		this()
		this.add(entityID)
		this.add(nbt)
	}

	override def getReceivableSide: Side = null

}
object PacketExtendedSync {
	abstract class Handler extends IMessageHandler[PacketExtendedSync, IMessage] {

		override def onMessage(req: PacketExtendedSync, ctx: MessageContext): IMessage = {

			val entityID = req.get[Int]
			val data = req.get[NBTTagCompound]

			ctx.side match {
				case Side.CLIENT => this.syncClient(entityID, data)
				case Side.SERVER => this.syncServer(entityID, data)
			}

			null
		}

		@SideOnly(Side.CLIENT)
		protected def syncClient(eID: Int, nbt: NBTTagCompound): Unit = {
			val mc = Minecraft.getMinecraft
			mc.addScheduledTask(new Runnable {
				override def run(): Unit = mc.theWorld.getEntityByID(eID) match {
					case e: Entity => deserialize(e, nbt)
					case _ =>
				}
			})
		}

		@SideOnly(Side.SERVER)
		protected def syncServer(eID: Int, nbt: NBTTagCompound): Unit = {
			val mc = FMLCommonHandler.instance().getMinecraftServerInstance
			mc.addScheduledTask(new Runnable {
				override def run(): Unit = mc.getEntityWorld.getEntityByID(eID) match {
					case e: Entity => deserialize(e, nbt)
					case _ =>
				}
			})
		}

		protected def deserialize(entity: Entity, nbt: NBTTagCompound): Unit

	}
}

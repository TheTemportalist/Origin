package temportalist.tardis.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}
import temportalist.origin.foundation.common.{IProxy, ModBase}
import temportalist.tardis.common.init.{ModEntities, ModItems}

/**
  *
  * Created by TheTemportalist on 5/27/2016.
  *
  * @author TheTemportalist
  */
@Mod(modid = Tardis.MODID, name = Tardis.NAME, version = Tardis.VERSION,
	modLanguage = "scala",
	guiFactory = Tardis.clientProxy,
	dependencies = "required-after:Forge;" + "required-after:origin;"
)
object Tardis extends ModBase {

	final val MODID = "tardis"
	final val NAME = "Tardis"
	final val VERSION = "@MOD_VERSION@"

	override def getDetails: IModDetails = this

	override def getModId: String = this.MODID

	override def getModName: String = this.NAME

	override def getModVersion: String = this.VERSION

	final val clientProxy = "temportalist.tardis.client.ProxyClient"
	final val serverProxy = "temportalist.tardis.server.ProxyServer"

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: IProxy = null

	override def getProxy: IProxy = this.proxy

	override def getOptions: OptionRegister = null

	override def getRegisters: Seq[Register] = Seq(ModItems, ModEntities)

	@Mod.EventHandler
	def pre(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	@Mod.EventHandler
	def post(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

}

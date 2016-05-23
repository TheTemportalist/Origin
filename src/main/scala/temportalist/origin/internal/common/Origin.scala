package temportalist.origin.internal.common

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import temportalist.origin.foundation.common.modTraits.IHasCommands
import temportalist.origin.foundation.common.registers.OptionRegister
import temportalist.origin.foundation.common.{IProxy, ModBase}
import temportalist.origin.foundation.server.ICommand
import temportalist.origin.internal.server.CommandPlayerData

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
@Mod(modid = Origin.MOD_ID, name = Origin.MOD_NAME, version = Origin.MOD_VERSION,
	modLanguage = "scala",
	guiFactory = Origin.proxyClient,
	dependencies = "required-after:Forge"
)
object Origin extends ModBase with IHasCommands {

	final val MOD_ID = "origin"
	final val MOD_NAME = "Origin"
	final val MOD_VERSION = "@MOD_VERSION@"
	final val proxyClient = "temportalist.origin.internal.client.ProxyClient"
	final val proxyServer = "temportalist.origin.internal.server.ProxyServer"

	/**
	  *
	  * @return A mod's name
	  */
	override def getModName: String = Origin.MOD_ID

	/**
	  *
	  * @return A mod's ID
	  */
	override def getModId: String = Origin.MOD_NAME

	/**
	  *
	  * @return A mod's version
	  */
	override def getModVersion: String = Origin.MOD_VERSION

	@SidedProxy(clientSide = this.proxyClient, serverSide = this.proxyServer)
	var proxy: IProxy = _

	override def getProxy: IProxy = this.proxy

	override def getOptions: OptionRegister = Options

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

	override def getCommands: Seq[ICommand] = Seq(CommandPlayerData)

}

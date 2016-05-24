package temportalist.origin.internal.common.handlers

import java.io.File
import java.util

import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import temportalist.origin.api.common.lib.ConfigJson
import temportalist.origin.api.common.resource.IModDetails
import temportalist.origin.foundation.common.register.OptionRegister

/**
 *
 *
 * @author TheTemportalist
 */
object OptionHandler {

	private val handlers: util.HashMap[String, OptionRegister] = new
					util.HashMap[String, OptionRegister]()

	def handleConfiguration(mod: IModDetails, options: OptionRegister,
			event: FMLPreInitializationEvent): Unit = {
		if (options.config == null) {
			val dir: File = options.getConfigDirectory(event.getModConfigurationDirectory)
			var cfgFile: File = null
			if (!options.hasDefaultConfig)
				options.customizeConfiguration(event)
			else
				options.getExtension match {
					case "cfg" =>
						cfgFile = new File(dir, mod.getModName + ".cfg")
						options.config = new Configuration(cfgFile, true)
					case "json" =>
						cfgFile = new File(dir, mod.getModName + ".json")
						options.config = new ConfigJson(cfgFile)
					case _ =>
				}
		}
		options.loadConfiguration()
		this.handlers.put(mod.getModID, options)
	}

	@SubscribeEvent
	def onConfigurationChanged(event: OnConfigChangedEvent): Unit = {
		val iterator: util.Iterator[String] = this.handlers.keySet().iterator()
		while (iterator.hasNext) {
			val pluginID: String = iterator.next()
			if (event.modID.equalsIgnoreCase(pluginID)) {
				this.handlers.get(pluginID).loadConfiguration()
			}
		}
	}

}

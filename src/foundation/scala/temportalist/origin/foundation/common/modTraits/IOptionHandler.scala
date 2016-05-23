package temportalist.origin.foundation.common.modTraits

import java.io.File

import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import temportalist.origin.api.common.lib.ConfigJson

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
trait IOptionHandler extends IHasDetails with IHasOptions {

	def handleConfiguration(event: FMLPreInitializationEvent): Unit = {
		val options = this.getOptions
		if (options == null) return

		val mod = this.getDetails
		if (options.config == null) {
			var cfgFile: File = null
			if (!options.hasDefaultConfig)
				options.customizeConfiguration(event)
			else {
				val dir: File = options.getConfigDirectory(event.getModConfigurationDirectory)
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

		}

		options.loadConfiguration()
	}

	def onConfigChange(event: OnConfigChangedEvent): Unit = {
		if (this.getOptions != null && event.getModID == this.getDetails.getModId)
			this.getOptions.loadConfiguration()
	}

}

package temportalist.origin.foundation.common.modTraits

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.IFuelHandler
import net.minecraftforge.fml.common.network.{IGuiHandler, NetworkRegistry}
import net.minecraftforge.fml.common.registry.GameRegistry

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
trait Registry {

	def registerHandler(handlers: Object*): Unit = {
		for (o: Object <- handlers) if (o != null) {
			MinecraftForge.EVENT_BUS.register(o)
		}
	}

	def registerFuelHandler(fuelHandlers: IFuelHandler*): Unit = {
		for (o: IFuelHandler <- fuelHandlers) if (o != null) {
			GameRegistry.registerFuelHandler(o)
		}
	}

	def registerGuiHandler(mod: AnyRef, handler: IGuiHandler): Unit = {
		NetworkRegistry.INSTANCE.registerGuiHandler(mod, handler)
	}

}

package com.temportalist.origin.api.common.register

import com.temportalist.origin.foundation.client.{KeyHandler, IKeyBinder}
import com.temportalist.origin.foundation.client.gui.IOverlay
import com.temportalist.origin.internal.client.gui.{EnumOverlay, OverlayHandler}
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.common.{FMLCommonHandler, IFuelHandler}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraftforge.common.MinecraftForge

/**
 *
 *
 * @author  TheTemportalist  5/7/15
 */
object Registry {

	def registerHandler(handlers: Object*): Unit = {
		for (o: Object <- handlers) if (o != null) {
			MinecraftForge.EVENT_BUS.register(o)
			FMLCommonHandler.instance().bus().register(o)
		}
	}

	def registerFuelHandler(fuelHandlers: IFuelHandler*): Unit = {
		for (o: IFuelHandler <- fuelHandlers) if (o != null) {
			GameRegistry.registerFuelHandler(o)
		}
	}

	@SideOnly(Side.CLIENT)
	def registerOverlay(overlay: IOverlay, types: EnumOverlay*): Unit = {
		OverlayHandler.register(overlay, types)
	}

	@SideOnly(Side.CLIENT)
	def registerKeyBinder(binder: IKeyBinder): Unit = {
		KeyHandler.register(binder)
	}

}

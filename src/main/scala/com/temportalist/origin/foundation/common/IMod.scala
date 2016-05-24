package com.temportalist.origin.foundation.common

import com.temportalist.origin.api.common.lib.LogHelper
import com.temportalist.origin.api.common.proxy.IProxy
import com.temportalist.origin.api.common.register._
import com.temportalist.origin.api.common.resource.IModDetails
import com.temportalist.origin.foundation.common.register.OptionRegister
import com.temportalist.origin.internal.common.handlers.OptionHandler
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.network.NetworkRegistry

import scala.reflect.classTag
import scala.util.Sorting

/**
 *
 *
 * @author TheTemportalist
 */
abstract class IMod extends NetworkMod {

	def log(message: String): Unit = LogHelper.info(this.getDetails.getModid, message)

	var options: OptionRegister = null
	private var sortedRegisters: Array[Register] = null

	protected def preInitialize(mod: IModDetails, event: FMLPreInitializationEvent, proxy: IProxy,
			options: OptionRegister, registers: Register*): Unit = {

		if (options != null) {
			this.options = options
			OptionHandler.handleConfiguration(mod, this.options, event)
		}

		this.sortedRegisters = Sorting.stableSort(registers)(classTag[Register], Register.Order)

		RegisterPhase.PREINIT_ORDER.foreach(phase => {
			this.sortedRegisters.foreach(reg => {
				Register.doRegistration(reg, phase, this.getDetails, event)
			}: Unit)
		}: Unit)

		Registry.registerHandler(this, proxy)

		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy)

	}

	protected def initialize(event: FMLInitializationEvent, proxy: IProxy): Unit = {
		this.sortedRegisters.foreach(reg => {
			Register.doRegistration(reg, RegisterPhase.INIT, this.getDetails, event)
		}: Unit)

		proxy.register()

	}

	protected def postInitialize(event: FMLPostInitializationEvent, proxy: IProxy): Unit = {
		proxy.postInit()
	}

}

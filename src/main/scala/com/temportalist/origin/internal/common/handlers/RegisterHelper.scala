package com.temportalist.origin.internal.common.handlers

import java.util
import com.temportalist.origin.api.common.register.Registry
import com.temportalist.origin.api.common.rendering.ISpriteMapper
import com.temportalist.origin.foundation.common.extended.ExtendedEntity
import com.temportalist.origin.internal.common.Origin
import com.temportalist.origin.internal.common.extended.ExtendedEntityHandler
import cpw.mods.fml.common.IFuelHandler
import net.minecraft.command.ICommand

import scala.collection.JavaConversions._

/**
 *
 *
 * @author TheTemportalist
 */
object RegisterHelper {

	@Deprecated
	def registerHandler(handlers: Object*): Unit = Registry.registerHandler(handlers)

	@Deprecated
	def registerFuelHandler(fuelHandler: IFuelHandler): Unit =
		Registry.registerFuelHandler(fuelHandler)

	def registerExtendedPlayer(classKey: String, extendedClass: Class[_ <: ExtendedEntity],
			deathPersistance: Boolean): Unit = {
		ExtendedEntityHandler.registerExtended(classKey, extendedClass, deathPersistance)
	}

	def registerSpritee(spritee: ISpriteMapper): Unit = {
		Origin.proxy.registerSpritee(spritee)
	}

	private val commands: util.List[ICommand] = new util.ArrayList[ICommand]()

	def registerCommand(command: ICommand): Unit = {
		this.commands.add(command)
	}

	def getCommands: scala.List[ICommand] = {
		this.commands.toList
	}

}

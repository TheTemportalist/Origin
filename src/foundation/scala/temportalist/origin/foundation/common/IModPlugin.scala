package temportalist.origin.foundation.common

import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent, FMLServerStartingEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.{FMLLog, Mod}
import org.apache.logging.log4j.Logger
import temportalist.origin.foundation.common.modTraits._
import temportalist.origin.foundation.common.network.NetworkMod
import temportalist.origin.foundation.common.registers.{Register, RegisterPhase}

import scala.reflect.classTag
import scala.util.Sorting

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
abstract class IModPlugin extends NetworkMod with IOptionHandler with Registry {

	private var logger: Logger = _

	@Deprecated
	final def log(str: String, data: AnyRef*): Unit = {
		this.log(str.asInstanceOf[Any], data:_*)
	}

	final def log(obj: Any, data: AnyRef*): Unit = {
		if (this.logger != null) {
			if (data.nonEmpty) {
				obj match {
					case str: String =>
						this.logger.info(str, data:_*)
						return
					case _ =>
				}
			}
			this.logger.info(obj)
		}
		else {
			FMLLog.info("[" + this.getDetails.getModId + "] " + obj, data:_*)
		}
	}

	def getRegisters: Seq[Register] = Seq()

	private var sortedRegisters: Array[Register] = _

	protected def preInitialize(event: FMLPreInitializationEvent): Unit = {
		this.logger = event.getModLog
		this.handleConfiguration(event)

		this.sortedRegisters = Sorting.stableSort(
			this.getRegisters)(classTag[Register], Register.Order)

		for (phase <- RegisterPhase.PREINIT_ORDER) {
			for (reg <- this.sortedRegisters) {
				if (phase == RegisterPhase.PREINIT_ORDER.head)
					reg.setMod(this.getDetails)
				Register.doRegistration(reg, phase, this.getDetails, event)
			}
		}

	}

	protected def initialize(event: FMLInitializationEvent): Unit = {
		this.sortedRegisters.foreach(reg => Register.doRegistration(reg, RegisterPhase.INIT, this.getDetails, event))
	}

	protected def postInitialize(event: FMLPostInitializationEvent): Unit = {
		this match {
			case tabContainer: IHasTabs =>
				tabContainer.initTabs()
			case _ =>
		}
	}

	@SubscribeEvent
	def onConfigurationChanged(event: OnConfigChangedEvent): Unit = this.onConfigChange(event)

	@Mod.EventHandler
	def serverStarting(event: FMLServerStartingEvent): Unit = {
		this match {
			case hazCommands: IHasCommands =>
				for (command <- hazCommands.getCommands)
					event.registerServerCommand(command)
			case _ =>
		}
	}

}

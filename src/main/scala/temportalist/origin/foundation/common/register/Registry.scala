package temportalist.origin.foundation.common.register

import net.minecraft.command.ICommand
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.IFuelHandler
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.api.common.block.BlockBase
import temportalist.origin.foundation.client.{KeyHandler, IKeyBinder}
import temportalist.origin.foundation.client.gui.IOverlay
import temportalist.origin.foundation.common.extended.ExtendedEntity
import temportalist.origin.internal.client.gui.{OverlayHandler, EnumOverlay}
import temportalist.origin.internal.common.extended.ExtendedEntityHandler

import scala.collection.mutable.ListBuffer

/**
 *
 *
 * @author  TheTemportalist  5/7/15
 */
object Registry {

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

	def registerExtendedPlayer(classKey: String, extendedClass: Class[_ <: ExtendedEntity],
			deathPersistence: Boolean): Unit = {
		ExtendedEntityHandler.registerExtended(classKey, extendedClass, deathPersistence)
	}

	private val commands = ListBuffer[ICommand]()

	def registerCommand(command: ICommand): Unit = {
		this.commands += command
	}

	def getCommands: ListBuffer[ICommand] = this.commands

	@SideOnly(Side.CLIENT)
	def registerOverlay(overlay: IOverlay, types: EnumOverlay*): Unit = {
		OverlayHandler.register(overlay, types)
	}

	@SideOnly(Side.CLIENT)
	def registerKeyBinder(binder: IKeyBinder): Unit = {
		KeyHandler.register(binder)
	}

	private val blockRegisters = ListBuffer[BlockRegister]()

	def addBlockRegister(register: BlockRegister): Unit = {
		this.blockRegisters += register
	}

	def getAllRegisteredBlocks: Array[BlockBase] = {
		val list = ListBuffer[BlockBase]()
		this.blockRegisters.foreach(reg => list ++= reg.getBlocks)
		list.toArray
	}

}

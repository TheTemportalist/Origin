package temportalist.origin.foundation.common.registers

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.origin.api.common.block.BlockBase

import scala.collection.mutable.ListBuffer

/**
 *
 *
 * @author TheTemportalist
 */
trait BlockRegister extends Register with ObjectRegistry[BlockBase] {

	override final def priority: Int = 1

	override final def getRegFuncType: Class[_ <: Register] = classOf[BlockRegister]

	/**
	 * This method is used to register TileEntities.
	 * Recommendation: Use GameRegistry.registerTileEntity
	 */
	def registerTileEntities(): Unit = {}

	/**
	 * This method is used to register crafting recipes
	 */
	def registerCrafting(): Unit = {}

	def registerSmelting(): Unit = {}

	def registerOther(): Unit = {}

	// ~~~~~~~~~~~ Register Funcs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final def register(id: String, clazz: Class[_ <: TileEntity]): Unit =
		GameRegistry.registerTileEntity(clazz, this.getMod.getModId + "_" + id)

}

package temportalist.origin.foundation.common.register

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.origin.api.common.block.BlockBase

import scala.collection.mutable.ListBuffer

/**
 *
 *
 * @author TheTemportalist
 */
trait BlockRegister extends Register {

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
		GameRegistry.registerTileEntity(clazz, this.getMod.getModID + ":" + id)

	// ~~~~~~~~~~~ Custom Models ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private val blocks = ListBuffer[BlockBase]()

	final def addBlock(block: BlockBase): Unit = {
		if (block.hasCustomItemModel) this.blocks += block
	}

	final def getBlocks: ListBuffer[BlockBase] = this.blocks

}

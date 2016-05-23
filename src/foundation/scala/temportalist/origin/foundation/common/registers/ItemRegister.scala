package temportalist.origin.foundation.common.registers

import temportalist.origin.api.common.item.ItemBase

/**
 *
 *
 * @author TheTemportalist
 */
trait ItemRegister extends Register with ObjectRegistry[ItemBase] {

	override final def priority: Int = 0

	override final def getRegFuncType: Class[_ <: Register] = classOf[ItemRegister]

	def registerItemsPostBlock(): Unit = {}

	def registerCrafting(): Unit = {}

	def registerSmelting(): Unit = {}

	def registerOther(): Unit = {}

}

package temportalist.origin.foundation.common.registers

import java.util

import net.minecraft.entity._
import net.minecraftforge.fml.common.registry.EntityRegistry

/**
 *
 *
 * @author TheTemportalist
 */
trait EntityRegister extends Register {

	override final def priority: Int = 2

	override final def getRegFuncType: Class[_ <: Register] = classOf[EntityRegister]

	var entID: Int = 0
	private val idMap: util.HashMap[Class[_ <: Entity], Int] =
		new util.HashMap[Class[_ <: Entity], Int]()

	protected final def addEntity(entityClass: Class[_ <: Entity], entityName: String,
			mod: Object): Unit = {
		this.addEntity(entityClass, entityName, mod, 80, 3, sendsVelocityUpdates = false)
	}

	/**
	 * @param entityClass The entity class
	 * @param entityName A unique name for the entity
	 * @param mod The mod
	 * @param trackingRange The range at which MC will send tracking updates
	 * @param updateFrequency The frequency of tracking updates
	 * @param sendsVelocityUpdates Whether to send velocity information packets as well
	 */
	protected final def addEntity(entityClass: Class[_ <: Entity], entityName: String, mod: Object,
			trackingRange: Int, updateFrequency: Int, sendsVelocityUpdates: Boolean): Unit = {
		EntityRegistry.registerModEntity(entityClass, entityName, entID, mod,
			trackingRange, updateFrequency, sendsVelocityUpdates
		)
		this.idMap.put(entityClass, entID)

		entID += 1
	}

	def addEntitySpawns(): Unit = {}

}

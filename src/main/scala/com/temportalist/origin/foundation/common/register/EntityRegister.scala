package com.temportalist.origin.foundation.common.register

import java.util

import com.temportalist.origin.api.common.register.Register
import com.temportalist.origin.internal.common.item.ItemEgg
import cpw.mods.fml.common.registry.EntityRegistry
import net.minecraft.entity._

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

	protected final def addEgg(
			entityClass: Class[_ <: Entity], primary: Int, secondary: Int
			): Unit = {
		ItemEgg.register(entityClass, primary, secondary)
	}

	def addEntitySpawns(): Unit = {}

	/*
	 *
	 * @param entityClass The class of the entity
	 * @param weightedProb The weighted probability that this entity will spawn
	 * @param min The minimum count of instances the spawn will call
	 * @param max The maximum count of instances the spawn will call
	 * @param typeOfCreature The creature type
	 * @param biomes A list of biomes that we can spawn in
	 */

}

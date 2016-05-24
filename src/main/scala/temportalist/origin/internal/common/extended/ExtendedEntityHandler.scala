package temportalist.origin.internal.common.extended

import java.util.UUID

import net.minecraft.entity.player.{EntityPlayerMP, EntityPlayer}
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.{EntityEvent, EntityJoinWorldEvent}
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.api.common.utility.WorldHelper
import temportalist.origin.foundation.common.extended.ExtendedEntity
import temportalist.origin.foundation.common.network.PacketExtendedSync
import temportalist.origin.internal.common.Origin

import scala.collection.mutable

/**
 * A handler class for the ExtendedEntity wrapper class.
 *
 * @author TheTemportalist
 */
object ExtendedEntityHandler {

	private val extendedProperties = mutable.Map[Class[_ <: ExtendedEntity], (String, Boolean)]()
	private val extendedKeys = mutable.Map[String, Class[_ <: ExtendedEntity]]()
	private val persistenceTags =
		mutable.Map[UUID, mutable.Map[Class[_ <: ExtendedEntity], NBTTagCompound]]()

	def getClassKey(extendedClass: Class[_ <: ExtendedEntity]): String =
		this.extendedProperties(extendedClass)._1

	def shouldSaveOnDeath(extendedClass: Class[_ <: ExtendedEntity]): Boolean =
		this.extendedProperties(extendedClass)._2

	/**
	 * Register an ExtendedEntity with a class key and a notice saying whether or not there is data
	 * that persists past death.
	 */
	def registerExtended(classKey: String, extendedClass: Class[_ <: ExtendedEntity],
			persistPastDeath: Boolean): Unit = {
		this.extendedProperties(extendedClass) = (classKey, persistPastDeath)
		this.extendedKeys(classKey) = extendedClass
	}

	final def getExtended[T <: ExtendedEntity](living: EntityPlayer,
			extendedClass: Class[T]): T = {
		if (living == null || !this.extendedProperties.contains(extendedClass))
			return null.asInstanceOf[T]
		living.getExtendedProperties(this.getClassKey(extendedClass)) match {
			case ext: ExtendedEntity => ext.asInstanceOf[T]
			case _ =>
				this.register(living, extendedClass)
				this.getExtended(living, extendedClass)
		}
	}

	final def getExtendedByKey(living: EntityPlayer, key: String): ExtendedEntity = {
		this.getExtended(living, this.extendedKeys(key))
	}

	final def register(entity: EntityPlayer, extendedClass: Class[_ <: ExtendedEntity]): Unit =
		this.register(entity, this.getClassKey(extendedClass), extendedClass)

	final def register(entity: EntityPlayer, classKey: String,
			extendedClass: Class[_ <: ExtendedEntity]): Unit = {
		if (entity.getExtendedProperties(classKey) != null) return
		val ent: ExtendedEntity = try {
			extendedClass.getConstructor(classOf[EntityPlayer]).newInstance(entity)
		} catch {
			case e: Exception => null
		}
		if (ent != null) {
			entity.registerExtendedProperties(classKey, ent)
		}
	}

	def updatePersistenceFor(uuid: UUID): Unit = {
		if (!this.persistenceTags.contains(uuid))
			this.persistenceTags(uuid) = mutable.Map[Class[_ <: ExtendedEntity], NBTTagCompound]()
	}

	def storeEntityData[T <: ExtendedEntity](extendedClass: Class[T], player: EntityPlayer,
			data: NBTTagCompound) {
		val uuid = player.getGameProfile.getId
		this.updatePersistenceFor(uuid)
		this.persistenceTags(uuid)(extendedClass) = data
	}

	def getDataAndRemove[T <: ExtendedEntity](
			extendedClass: Class[T], player: EntityPlayer): NBTTagCompound = {
		val uuid: UUID = player.getGameProfile.getId
		this.updatePersistenceFor(uuid)
		if (this.persistenceTags(uuid).contains(extendedClass))
			this.persistenceTags(uuid).remove(extendedClass).get
		else null
	}

	/**
	 * Control the creation of ExtendedEntities for each player (only if they do not already have
	 * that ExtendedEntity)
	 */
	@SubscribeEvent
	def onEntityConstructing(event: EntityEvent.EntityConstructing) {
		event.entity match {
			case living: EntityPlayer =>
				this.extendedProperties.foreach(seq => {
					this.register(living, seq._2._1, seq._1)
				})
			case _ =>
		}
	}

	@SubscribeEvent
	def onLivingDeath(event: LivingDeathEvent): Unit = {
		event.entityLiving match {
			case player: EntityPlayer => if (WorldHelper.isServer(player)) {
				this.extendedProperties.foreach(seq => if (seq._2._2) {
					val data = new NBTTagCompound
					this.getExtended(player, seq._1).saveNBTData(data)
					this.storeEntityData(seq._1, player, data)
				})
			}
			case _ =>
		}
	}

	@SubscribeEvent
	def onEntityJoinWorld(event: EntityJoinWorldEvent): Unit = {
		event.entity match {
			case player: EntityPlayer => if (WorldHelper.isServer(player)) {
				this.extendedProperties.foreach(seq => if (seq._2._2) {
					this.getExtended(player, seq._1) match {
						case extended: ExtendedEntity =>
							val data = this.getDataAndRemove(seq._1, player)
							if (data != null) extended.loadNBTData(data)
							//extended.syncEntityFull()
						case _ =>
					}
				})
			}
			case _ =>
		}
	}

	@SubscribeEvent
	def onPlayerLogin(event: PlayerLoggedInEvent): Unit = {
		if (!event.player.getEntityWorld.isRemote) {
			this.extendedProperties.keys.foreach(extClass => {
				if (extClass != null) {
					this.getExtended(event.player, extClass) match {
						case extended: ExtendedEntity =>
							val nbt = new NBTTagCompound
							extended.saveNBTData(nbt)
							new PacketExtendedSync(extClass, "").add(nbt).sendToPlayer(
								Origin, event.player.asInstanceOf[EntityPlayerMP])
						case _ =>
					}
				}
			})
		}
	}

}

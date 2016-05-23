package temportalist.origin.foundation.common.capability

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.{NBTBase, NBTTagCompound}
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.common.capabilities.Capability.IStorage
import net.minecraftforge.common.capabilities.{Capability, CapabilityManager, ICapabilityProvider, ICapabilitySerializable}
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.foundation.common.network.{NetworkMod, PacketExtendedSync}

/**
  *
  * Created by TheTemportalist on 5/6/2016.
  *
  * @author TheTemportalist
  */
abstract class ExtendedHandler[N <: NBTBase, I <: INBTSerializable[N], C <: I, E <: ICapabilityProvider](
		private val interfaceClass: Class[I], private val implClass: Class[C],
		private val targetClass: Class[E]
) extends ICapabilitySerializable[N] {

	private var key: ResourceLocation = _

	final def get(player: E): I = player.getCapability(this.getCapabilityObject, null)

	/**
	  * @return a trait/interface that is the super of [[getDefaultImplementationClass]]
	  */
	def getInterfaceClass: Class[I] = this.interfaceClass

	/**
	  * @return the class of [[getDefaultImplementation]]
	  */
	def getDefaultImplementationClass: Class[C] = this.implClass

	/**
	  * @return the implementation of [[getInterfaceClass]]
	  */
	def getDefaultImplementation: I

	def getNewImplementation(obj: E): I

	/**
	  * @return the @CapabilityInject object (which should have passed the class of [[getInterfaceClass]])
	  */
	def getCapabilityObject: Capability[I]

	final def init(mod: IModPlugin, locationPath: String): Unit = {

		this.key = new ResourceLocation(mod.getDetails.getModId, locationPath)
		mod.registerHandler(this)

		CapabilityManager.INSTANCE.register(this.getInterfaceClass,
			new IStorage[I] {

				override def writeNBT(capability: Capability[I], instance: I,
						side: EnumFacing): NBTBase = instance.serializeNBT()

				override def readNBT(capability: Capability[I], instance: I,
						side: EnumFacing, nbt: NBTBase): Unit =
					instance.deserializeNBT(nbt.asInstanceOf[N])

			}, this.getDefaultImplementationClass
		)

		this.initOptional(mod)

	}

	def initOptional(mod: IModPlugin): Unit = {}

	final def getKey: ResourceLocation = this.key

	def isValid(e: ICapabilityProvider): Boolean

	def cast(e: ICapabilityProvider): E

	override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
		capability == this.getCapabilityObject
	}

	override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
		if (this.hasCapability(capability, facing))
			this.getCapabilityObject.cast(this.getDefaultImplementation)
		else null.asInstanceOf[T]
	}

	override def serializeNBT(): N = this.getDefaultImplementation.serializeNBT()

	override def deserializeNBT(nbt: N): Unit = this.getDefaultImplementation.deserializeNBT(nbt)

}
object ExtendedHandler {

	abstract class ExtendedEntity[N <: NBTTagCompound, I <: INBTSerializable[N], C <: I, E <: ICapabilityProvider](
			interfaceClass: Class[I], implClass: Class[C], targetClass: Class[E]
	) extends ExtendedHandler[N, I, C, E](
		interfaceClass, implClass, targetClass
	) {

		private var networkMod: NetworkMod = null

		override def initOptional(mod: IModPlugin): Unit = {
			mod match {
				case network: NetworkMod =>
					this.networkMod = network
					network.registerMessage(this.getPacketHandlingClass, classOf[PacketExtendedSync])
				case _ =>
			}
		}

		def getPacketHandlingClass: Class[_ <: PacketExtendedSync.Handler]

		// ~~~~~~~~~~ Death Persistence ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		def doesDataPersistDeath: Boolean = false

		@SubscribeEvent
		final def onPlayerClone(event: PlayerEvent.Clone): Unit = {
			if (!this.doesDataPersistDeath || !event.isWasDeath ||
					!classOf[EntityPlayer].isAssignableFrom(this.targetClass)) return
			val data = this.get(event.getOriginal.asInstanceOf[E]).serializeNBT
			this.get(event.getEntityPlayer.asInstanceOf[E]).deserializeNBT(data)
		}

		// ~~~~~~~~~~ Syncing Server data to Client ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		@SubscribeEvent
		final def onEntityJoinWorld(event: EntityJoinWorldEvent): Unit = {
			if (event.getWorld.isRemote) return
			if (this.networkMod == null) return

			val entity = event.getEntity
			if (this.isValid(entity)) {
				val data = this.get(entity.asInstanceOf[E]).serializeNBT()
				new PacketExtendedSync(
					entity.getEntityId, data
				).sendToDimension(this.networkMod, event.getWorld.provider.getDimension)
			}

		}

		// ~~~~~~~~~~ Capability Attaching ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		@SubscribeEvent
		def attachCapabilities(event: AttachCapabilitiesEvent.Entity): Unit = {
			val entity = event.getEntity
			if (this.isValid(entity)) {
				val e = this.cast(entity)
				if (this.getCapabilityObject == null) {
					FMLLog.info("ERROR: Capability for " + this.getKey.toString + " is NULL!!!")
					return
				}
				val obj = this
				FMLLog.info("Attaching capability entity " + this.getKey.toString + " to " + e.getClass.getCanonicalName)
				event.addCapability(this.getKey, new ICapabilitySerializable[N] {

					private var instance = obj.getDefaultImplementation
					if (instance == null) instance = obj.getNewImplementation(e)

					override def deserializeNBT(nbt: N): Unit = instance.deserializeNBT(nbt)

					override def serializeNBT(): N = instance.serializeNBT()

					override def hasCapability(
							capability: Capability[_], facing: EnumFacing): Boolean =
						obj.hasCapability(capability, facing)

					override def getCapability[T](
							capability: Capability[T], facing: EnumFacing): T =
						if (this.hasCapability(capability, facing))
							obj.getCapabilityObject.cast(this.instance)
						else null.asInstanceOf[T]

				})
			}
		}

	}

	abstract class ExtendedTile[N <: NBTBase, I <: INBTSerializable[N], C <: I, E <: ICapabilityProvider](
			interfaceClass: Class[I], implClass: Class[C], targetClass: Class[E]
	) extends ExtendedHandler[N, I, C, E](
		interfaceClass, implClass, targetClass
	) {
		@SubscribeEvent
		def attachCapabilities(event: AttachCapabilitiesEvent.TileEntity): Unit = {
			val tile = event.getTileEntity
			if (this.isValid(tile)) {
				val e = this.cast(tile)
				if (this.getCapabilityObject == null) {
					FMLLog.info("ERROR: Capability for " + this.getKey.toString + " is NULL!!!")
					return
				}
				FMLLog.info("Attaching capability tile " + this.getKey.toString + " to " + e.getClass.getCanonicalName)
				event.addCapability(this.getKey, this)
			}
		}
	}

	abstract class ExtendedItem[N <: NBTBase, I <: INBTSerializable[N], C <: I, E <: ICapabilityProvider](
			interfaceClass: Class[I], implClass: Class[C], targetClass: Class[E]
	) extends ExtendedHandler[N, I, C, E](
		interfaceClass, implClass, targetClass
	) {

		def isValidItem(item: Item, stack: ItemStack): Boolean

		@SubscribeEvent
		def attachCapabilities(event: AttachCapabilitiesEvent.Item): Unit = {
			val item = event.getItem
			if (this.isValidItem(item, event.getItemStack)) {
				if (this.getCapabilityObject == null) {
					FMLLog.info("ERROR: Capability for " + this.getKey.toString + " is NULL!!!")
					return
				}
				FMLLog.info("Attaching capability item " + this.getKey.toString + " to " + item.getClass.getCanonicalName)
				event.addCapability(this.getKey, this)
			}
		}

	}

}

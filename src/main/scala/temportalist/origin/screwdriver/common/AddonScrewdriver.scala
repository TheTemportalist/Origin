package temportalist.origin.screwdriver.common

import com.temportalist.origin.api.common.register.Registry
import com.temportalist.origin.api.common.resource.{EnumResource, IModDetails, IModResource}
import com.temportalist.origin.foundation.common.IMod
import com.temportalist.origin.internal.common.Origin
import com.temportalist.origin.screwdriver.api.{ApiOriginScrewdriver, Behavior, BehaviorType}
import com.temportalist.origin.screwdriver.common.behaviors._
import com.temportalist.origin.screwdriver.common.behaviors.datacore.{BehaviorDataCore, BehaviorScanner, DataCoreHandler, EntityState}
import com.temportalist.origin.screwdriver.common.behaviors.enderio.{BehaviorEnderIOConduitVisibility, BehaviorEnderIOFacadeVisibility}
import com.temportalist.origin.screwdriver.common.behaviors.immersiveengineering.{BehaviorIEHammer, BehaviorIEWireCutter}
import com.temportalist.origin.screwdriver.common.items.ItemScrewdriver
import com.temportalist.origin.screwdriver.common.network.{PacketOpenGui, PacketUpdateItem}
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.common.{Mod, SidedProxy}
import cpw.mods.fml.relauncher.Side
import net.minecraft.init.Items
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.{NBTTagCompound, NBTTagList, NBTTagString}
import net.minecraft.world.World
import net.minecraftforge.oredict.ShapedOreRecipe

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by TheTemportalist on 12/20/2015.
 */
@Mod(modid = AddonScrewdriver.MOD_ID, name = AddonScrewdriver.MOD_NAME,
	version = AddonScrewdriver.MOD_VERSION,
	//THIS IS FOR CONFIG guiFactory = AddonScrewdriver.clientProxy,
	modLanguage = "scala",
	dependencies = "required-after:origin" +
			";after:appliedenergistics2" +
			";after:BuildCraft|Core" +
			";after:CoFHLib" +
			";after:EnderIO" +
			";after:Railcraft" +
			";after:MineFactoryReloaded" +
			";after:ImmersiveEngineering"
)
object AddonScrewdriver extends IMod with IModResource {

	final val MOD_ID = "addonscrewdriver"
	final val MOD_NAME = "OriginAddonScrewdriver"
	final val MOD_VERSION = "1.0.0"
	final val clientProxy = "com.temportalist.origin.screwdriver.client.ProxyClient"
	final val serverProxy = "com.temportalist.origin.screwdriver.server.ProxyServer"

	override def getModid: String = AddonScrewdriver.MOD_ID

	override def getModVersion: String = AddonScrewdriver.MOD_VERSION

	override def getModName: String = AddonScrewdriver.MOD_NAME

	override def getDetails: IModDetails = this

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: ProxyCommon = null

	var screwdriver: ItemScrewdriver = null
	var screwdrivers: Array[ItemStack] = null
	val TIERS_OF_SCREWDRIVERS = 8
	var BEHAVIOR_SETTINGS_GLOBAL_ID = -1

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		Registry.registerHandler(DataCoreHandler)
		ApiOriginScrewdriver.preInit(this)
		this.preInitialize(this, event, this.proxy, null)

		this.registerNetwork()
		this.registerPacket(classOf[PacketOpenGui.Handler],
			classOf[PacketOpenGui], Side.SERVER)
		this.registerPacket(classOf[PacketUpdateItem.Handler],
			classOf[PacketUpdateItem], Side.SERVER)

		this.screwdriver = new ItemScrewdriver
		Origin.addItemToTab(this.screwdriver)
		this.registerRecipes()

		this.BEHAVIOR_SETTINGS_GLOBAL_ID = BehaviorSettings.register()
		BehaviorScanner.register()
		BehaviorDataCore.register()
		BehaviorWrench.register()
		BehaviorShears.register()
		BehaviorRailcraftCrowbar.register()
		BehaviorMFRHighlight.register()
		BehaviorEnderIOConduitVisibility.register()
		BehaviorEnderIOFacadeVisibility.register()
		BehaviorIEHammer.register()
		BehaviorIEWireCutter.register()

		AddonScrewdriver.loadResource("gui_behaviors",
			(EnumResource.GUI, "screwdriver/behaviors.png"))
		for (i <- 0 to 3)
			AddonScrewdriver.loadResource("gui_datacore_" + i,
				(EnumResource.GUI, "screwdriver/datacore_" + i + ".png"))
	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event, this.proxy)
	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event, this.proxy)
		this.behavior_GlobalIDToObject.values.foreach(behavior => behavior.postInit())
		DataCoreHandler.refreshData()
	}

	def registerRecipes(): Unit = {
		this.screwdrivers = new Array[ItemStack](this.TIERS_OF_SCREWDRIVERS)
		val baseScrewdriver = AddonScrewdriver.NBTBehaviorHelper.getFreshScrewdriver
		for (i <- 0 until this.TIERS_OF_SCREWDRIVERS) {
			val stack = baseScrewdriver.copy()
			stack.setItemDamage(i)
			this.screwdrivers(i) = stack
		}


		var tier = 0
		GameRegistry.addRecipe(new ShapedOreRecipe(this.screwdrivers(tier),
			" f ", " sf", "s  ",
			Char.box('f'), Items.flint,
			Char.box('s'), "stickWood"
		))
		tier += 1
		GameRegistry.addRecipe(new ShapedOreRecipe(this.screwdrivers(tier),
			" i ", "idi", " i ",
			Char.box('d'), this.screwdrivers(tier - 1),
			Char.box('i'), "ingotIron"
		))
		tier += 1
		GameRegistry.addRecipe(new ShapedOreRecipe(this.screwdrivers(tier),
			" r", "d ",
			Char.box('d'), this.screwdrivers(tier - 1),
			Char.box('r'), "blockRedstone"
		))
		tier += 1
		GameRegistry.addRecipe(new ShapedOreRecipe(this.screwdrivers(tier),
			" l", "d ",
			Char.box('d'), this.screwdrivers(tier - 1),
			Char.box('l'), "gemDiamond"
		))
		tier += 1
		GameRegistry.addRecipe(new ShapedOreRecipe(this.screwdrivers(tier),
			"q e", "qd ", "qqq",
			Char.box('d'), this.screwdrivers(tier - 1),
			Char.box('q'), Items.leather,
			Char.box('e'), "gemEmerald"
		))
		tier += 1
		GameRegistry.addRecipe(new ShapedOreRecipe(this.screwdrivers(tier),
			"   ", "od ", " o ",
			Char.box('d'), this.screwdrivers(tier - 1),
			Char.box('o'), "blockIron"
		))
		tier += 1
		GameRegistry.addRecipe(new ShapedOreRecipe(this.screwdrivers(tier),
			"opo", " d ", "   ",
			Char.box('d'), this.screwdrivers(tier - 1),
			Char.box('o'), "blockIron",
			Char.box('p'), "blockEmerald"
		))
		tier += 1
		GameRegistry.addRecipe(new ShapedOreRecipe(this.screwdrivers(tier),
			" t ", " t ", " d ",
			Char.box('d'), this.screwdrivers(tier - 1),
			Char.box('t'), "blockLapis"
		))

	}

	/**
	 * Maps a BehaviorType ID to a list of Behavior global IDs
	 */
	val behaviors = Array.fill(BehaviorType.values().length) {ListBuffer[Int]()}
	/**
	 * Inverse of behavior_NameToGlobalID
	 */
	val behavior_GlobalIDToName = mutable.Map[Int, String]()
	/**
	 * Maps a Behavior's global ID to the behavior object
	 */
	val behavior_GlobalIDToObject = mutable.Map[Int, Behavior]()
	/**
	 * Maps a Behavior's global ID to the behavior object
	 */
	val behavior_GlobalIDToRadial = mutable.Map[Int, BehaviorWrap]()
	/**
	 * Lists default behaviors by global ID
	 */
	val defaultBehaviors = ListBuffer[Int]()

	val GUI_BEHAVIORS = 0
	val GUI_DATA_CORE = 1

	def registerBehavior(behavior: Behavior): Int = {
		val id = this.behavior_GlobalIDToName.size
		this.behavior_GlobalIDToName(id) = behavior.getName
		this.behavior_GlobalIDToObject(id) = behavior
		this.behavior_GlobalIDToRadial(id) = new BehaviorWrap(behavior)
		if (behavior.isDefaultBehavior) this.defaultBehaviors += id
		this.behaviors(behavior.getBehaviorType.getID) += id
		AddonScrewdriver.log("Registered Behavior " + behavior.getName + " with id " + id)
		id
	}

	def getBehaviorGlobalID(name: String): Int = {
		ApiOriginScrewdriver.getBehaviorGlobalID(name)
	}

	def getBehaviorByGlobalID(globalID: Int): Behavior = {
		if (globalID >= 0) AddonScrewdriver.behavior_GlobalIDToObject(globalID)
		else null
	}

	def getRadialBehaviorByGlobalID(globalID: Int): BehaviorWrap = {
		if (globalID >= 0) AddonScrewdriver.behavior_GlobalIDToRadial(globalID)
		else null
	}

	def getBehaviorNameByGlobalID(globalID: Int): String = {
		if (globalID >= 0) this.behavior_GlobalIDToName(globalID)
		else null
	}

	def getBehaviorIDsProvided(stack: ItemStack): ListBuffer[Int] = {
		val behaviors = ListBuffer[Int]()
		if (stack.getItem == this.screwdriver) return behaviors
		this.behavior_GlobalIDToObject.foreach(idToB =>
			if (!idToB._2.isDefaultBehavior &&
					idToB._2.isValidStackForSimulation(stack)) behaviors += idToB._1
		)
		behaviors
	}

	object NBTBehaviorHelper {

		val HOT_BAR_SIZE = 10

		def getFreshScrewdriver: ItemStack = {
			val stack = new ItemStack(AddonScrewdriver.screwdriver)
			this.updateModuleBehaviors(stack, null)
			stack
		}

		private def getDefaultTag(populated: Boolean = false): NBTTagCompound = {
			val tagCom = new NBTTagCompound

			tagCom.setTag("obtained", {
				val tag = new NBTTagCompound
				BehaviorType.values().foreach(behaviorType => {
					val behaviorsOfType = AddonScrewdriver.behaviors(behaviorType.getID)
					val globalIDs =
						if (populated) behaviorsOfType.toArray
						else if (AddonScrewdriver.defaultBehaviors.nonEmpty) {
							val list = ListBuffer[Int]()
							AddonScrewdriver.defaultBehaviors.foreach(globalID => {
								if (AddonScrewdriver.behavior_GlobalIDToObject(globalID).
										getBehaviorType == behaviorType)
									list += globalID
							})
							list.toArray
						}
						else Array.fill(behaviorsOfType.length) {-1}
					tag.setIntArray(behaviorType.getKey, globalIDs)
				})
				tag
			})

			tagCom.setIntArray("hotbar", Array.fill(HOT_BAR_SIZE - 1) {-1})
			tagCom.setInteger("currentActive", -1)
			tagCom.setIntArray("toggled", Array[Int]())
			tagCom.setTag("modules", new NBTTagList)
			tagCom.setTag("entities_scanned", new NBTTagList)

			tagCom
		}

		private def checkNBT(stack: ItemStack, populated: Boolean = false): Unit = {
			if (!stack.hasTagCompound) this.resetData(stack, populated)
		}

		private def resetData(stack: ItemStack, populated: Boolean = false): Unit = {
			stack.setTagCompound(this.getDefaultTag(populated))
		}

		private def getObtainedBehaviors(stack: ItemStack): NBTTagCompound = {
			this.checkNBT(stack)
			stack.getTagCompound.getCompoundTag("obtained")
		}

		def getObtainedBehaviors(stack: ItemStack,
				behaviorType: BehaviorType): Array[Int] = {
			this.getObtainedBehaviors(stack).getIntArray(behaviorType.getKey)
		}

		private def setObtainedBehaviors(stack: ItemStack,
				behaviorType: BehaviorType, data: Array[Int]): Unit = {
			this.getObtainedBehaviors(stack).setIntArray(behaviorType.getKey, data)
		}

		def hasBehavior(stack: ItemStack, behavior: Behavior): Boolean = {
			this.getObtainedBehaviors(stack,
				behavior.getBehaviorType).contains(behavior.getGlobalID)
		}

		def obtainBehavior(stack: ItemStack, behavior: Behavior): Unit = {
			val obtainedOfType = ListBuffer[Int](
				this.getObtainedBehaviors(stack, behavior.getBehaviorType): _*)
			if (obtainedOfType.contains(behavior.getGlobalID)) return
			obtainedOfType += behavior.getGlobalID
			this.setObtainedBehaviors(stack, behavior.getBehaviorType, obtainedOfType.toArray)
			if (behavior.getBehaviorType == BehaviorType.TOGGLE)
				this.toggleBehavior(stack, behavior, forced = true, default = true)
		}

		def getHotBarSize(stack: ItemStack): Int = {
			// 1 = 3
			// 5 = 8
			// 7 = 10
			stack.getItemDamage + 3
		}

		def getInventorySize(stack: ItemStack): Int = {
			// 1 = 2
			// 2 = 4
			// 3 = 6
			// 4 = 8
			// 5 = 10
			// 6 = 11
			// 7 = 12
			val meta = stack.getItemDamage
			Math.min(meta, 5) * 2 + Math.max(meta - 5, 0)
		}

		def getBehaviorSize(stack: ItemStack, behaviorType: BehaviorType): Int = {
			behaviorType match {
				case BehaviorType.ACTIVE => 12
				case BehaviorType.TOGGLE => 9
				case BehaviorType.PASSIVE => 16
				case _ => 0
			}
		}

		def getHotBarGlobalIDs(stack: ItemStack): Array[Int] = {
			val maxQuantity = 10
			val hotBar = ListBuffer[Int](BehaviorSettings.getGlobalID)
			/*
			val activeBehaviors = this.getObtainedBehaviors(stack, BehaviorType.ACTIVE)
			var i = 0
			do {
				hotBar += (if (i < activeBehaviors.length &&
						!hotBar.contains(activeBehaviors(i))) activeBehaviors(i) else -1)
				i += 1
			} while (hotBar.length < maxQuantity)
			*/
			this.checkNBT(stack)
			val hotBarTagArray = stack.getTagCompound.getIntArray("hotbar")
			hotBar ++= hotBarTagArray
			hotBar.toArray
		}

		def getHotBarAsBehaviors(stack: ItemStack): Array[BehaviorWrap] = {
			val hotBar = ListBuffer[BehaviorWrap]()
			this.getHotBarGlobalIDs(stack).foreach(globalID =>
				hotBar += AddonScrewdriver.getRadialBehaviorByGlobalID(globalID))
			hotBar.toArray
		}

		def putInHotBar(stack: ItemStack, index: Int, behavior: Behavior): Unit = {
			this.putInHotBar(stack, index, behavior.getGlobalID)
		}

		def putInHotBar(stack: ItemStack, index: Int, globalID: Int): Unit = {
			this.checkNBT(stack)
			if (index > 0 && index < HOT_BAR_SIZE) {
				val hotBarArray = stack.getTagCompound.getIntArray("hotbar")
				hotBarArray(index - 1) = globalID
				stack.getTagCompound.setIntArray("hotbar", hotBarArray)
			}
		}

		def getCurrentActiveBehaviorID(stack: ItemStack): Int = {
			this.checkNBT(stack)
			stack.getTagCompound.getInteger("currentActive")
		}

		def getCurrentActiveBehavior(stack: ItemStack): Behavior = {
			AddonScrewdriver.getBehaviorByGlobalID(this.getCurrentActiveBehaviorID(stack))
		}

		def setCurrentActiveBehavior(stack: ItemStack, behavior: Behavior): Unit = {
			this.setCurrentActiveBehavior(stack, behavior.getGlobalID)
		}

		def setCurrentActiveBehavior(stack: ItemStack, globalID: Int): Unit = {
			this.checkNBT(stack)
			stack.getTagCompound.setInteger("currentActive", globalID)
		}

		def toggleBehavior(stack: ItemStack, behavior: Behavior, forced: Boolean = false,
				default: Boolean = false): Unit = {
			if (behavior.getBehaviorType != BehaviorType.TOGGLE) return
			if (this.hasBehavior(stack, behavior)) {
				val id = behavior.getGlobalID
				val toggled = ListBuffer(stack.getTagCompound.getIntArray("toggled"): _ *)
				val isToggled = toggled contains id
				def add: Unit = toggled += id
				def remove: Unit = toggled.remove(toggled.indexOf(id))
				if (forced) {
					if (default) {
						if (!isToggled) add
					}
					else {
						if (isToggled) remove
					}
				}
				else {
					if (isToggled) remove
					else add
				}
				toggled.sorted
				stack.getTagCompound.setIntArray("toggled", toggled.toArray)
			}
		}

		def isToggledEnabled(stack: ItemStack, behavior: Behavior): Boolean = {
			if (behavior.getBehaviorType != BehaviorType.TOGGLE) return false
			if (this.hasBehavior(stack, behavior)) {
				stack.getTagCompound.getIntArray("toggled") contains behavior.getGlobalID
			}
			else false
		}

		def isStackValidAsModule(potentialStack: ItemStack): Boolean = {
			AddonScrewdriver.getBehaviorIDsProvided(potentialStack).nonEmpty
		}

		def updateModuleBehaviors(stack: ItemStack, inv: Array[ItemStack]): Unit = {
			val newBehaviors = ListBuffer[Int]()
			if (inv != null) inv.foreach(invStack => {
				if (invStack != null)
					AddonScrewdriver.getBehaviorIDsProvided(invStack).foreach(globalID =>
						if (!newBehaviors.contains(globalID)) newBehaviors += globalID)
			})
			val old_current = this.getCurrentActiveBehaviorID(stack)
			val old_toggled_IDs = stack.getTagCompound.getIntArray("toggled")
			val old_hotbar = this.getHotBarGlobalIDs(stack)
			this.resetData(stack)

			old_hotbar.foreach(globalID => {
				if (AddonScrewdriver.defaultBehaviors.contains(globalID) ||
						newBehaviors.contains(globalID))
					this.putInHotBar(stack, old_hotbar.indexOf(globalID), globalID)
			})
			newBehaviors.foreach(globalID => {
				val behavior = AddonScrewdriver.getBehaviorByGlobalID(globalID)
				this.obtainBehavior(stack, behavior)
				this.toggleBehavior(stack, behavior, forced = true,
					default = old_toggled_IDs contains globalID)
			})
			if (!newBehaviors.contains(old_current)) this.setCurrentActiveBehavior(stack, -1)
		}

		private def readTagForItemStack(nBTTagCompound: NBTTagCompound): (Int, ItemStack) = {
			(nBTTagCompound.getByte("slot") & 255, ItemStack.loadItemStackFromNBT(nBTTagCompound))
		}

		def getModules(stack: ItemStack): Array[ItemStack] = {
			this.checkNBT(stack)
			val tagList = stack.getTagCompound.getTagList("modules", 10)
			val ret = new Array[ItemStack](tagList.tagCount())
			for (i <- 0 until tagList.tagCount())
				ret(i) = this.readTagForItemStack(tagList.getCompoundTagAt(i))._2
			ret
		}

		def getFirstStackThatMatches(stack: ItemStack, behavior: Behavior): (Int, ItemStack) = {
			this.checkNBT(stack)
			val tagList = stack.getTagCompound.getTagList("modules", 10)
			for (i <- 0 until tagList.tagCount()) {
				val indexedStack = this.readTagForItemStack(tagList.getCompoundTagAt(i))
				if (behavior.isValidStackForSimulation(indexedStack._2))
					return indexedStack
			}
			(-1, null)
		}

		def writeModules(stack: ItemStack, modules: Array[ItemStack]): Unit = {
			val tagList = new NBTTagList
			for (i <- modules.indices) {
				val stack = modules(i)
				if (stack != null) {
					val tagCom = new NBTTagCompound
					tagCom.setByte("slot", i.toByte)
					stack.writeToNBT(tagCom)
					tagList.appendTag(tagCom)
				}
			}
			stack.getTagCompound.setTag("modules", tagList)
		}

		private def writeModule(index: Int, module: ItemStack): NBTTagCompound = {
			val tagCom = new NBTTagCompound
			tagCom.setByte("slot", index.toByte)
			module.writeToNBT(tagCom)
			tagCom
		}

		def setModule(stack: ItemStack, index: Int, module: ItemStack): Unit = {
			this.checkNBT(stack)
			val tagList = stack.getTagCompound.getTagList("modules", 10)
			val newTagList = new NBTTagList
			var didSetModule = false
			for (i <- 0 until tagList.tagCount()) {
				val tagCom = tagList.getCompoundTagAt(i)
				val slot = tagCom.getByte("slot") & 255
				if (slot == index) {
					newTagList.appendTag(this.writeModule(index, module))
					didSetModule = true
				}
				else newTagList.appendTag(tagCom)
			}
			if (!didSetModule) {
				newTagList.appendTag(this.writeModule(index, module))
			}
			stack.getTagCompound.setTag("modules", newTagList)
		}

		def addScannedEntity(stack: ItemStack, canonicalName: String): Unit = {
			this.checkNBT(stack)
			val tagList = stack.getTagCompound.getTagList("entities_scanned", 8)
			tagList.appendTag(new NBTTagString(canonicalName))
			stack.getTagCompound.setTag("entities_scanned", tagList)
		}

		private def getScanned[U](stack: ItemStack, onEachClassName: (String) => U): Unit = {
			this.checkNBT(stack)
			val tagList = stack.getTagCompound.getTagList("entities_scanned", 8)
			for (i <- 0 until tagList.tagCount()) onEachClassName(tagList.getStringTagAt(i))
		}

		def getScannedEntityClassNames(stack: ItemStack): Array[String] = {
			val names = ListBuffer[String]()
			this.getScanned(stack, canonicalName => names += canonicalName)
			names.toArray
		}

		def getScannedEntityStates(stack: ItemStack): Array[EntityState] = {
			val states = ListBuffer[EntityState]()
			this.getScanned(stack, canonicalName => {
				val state = DataCoreHandler.getEntityState(canonicalName)
				if (state != null) states += state
			})
			states.toArray
		}

		def printBehaviors(stack: ItemStack): Unit = {
			var string = "\n"
			BehaviorType.values().foreach(bType => {
				string += bType.getKey + "\n"
				this.getObtainedBehaviors(stack, bType).foreach(globalID => {
					string += "  " + AddonScrewdriver.getBehaviorNameByGlobalID(globalID) + "\n"
				})
			})
			print(string)
		}

	}

}

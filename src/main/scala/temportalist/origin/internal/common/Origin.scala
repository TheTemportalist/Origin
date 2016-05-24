package temportalist.origin.internal.common

import java.util
import java.util.UUID

import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.enchantment.Enchantment
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util._
import net.minecraft.world.WorldServer
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent, FMLServerStartingEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import net.minecraftforge.fml.relauncher.Side
import temportalist.origin.api.common.item.ItemPlacer
import temportalist.origin.api.common.resource.{EnumResource, IModDetails, IModResource}
import temportalist.origin.foundation.common.IMod
import temportalist.origin.foundation.common.network._
import temportalist.origin.foundation.common.network.PacketTeleport.Handler
import temportalist.origin.foundation.common.register.Registry
import temportalist.origin.internal.common.extended.ExtendedEntityHandler
import temportalist.origin.internal.common.handlers.OptionHandler
import temportalist.origin.internal.common.item.ItemEgg
import temportalist.origin.internal.server.command.{CommandOpLevel, CommandOrigin}

/**
 *
 *
 * @author TheTemportalist
 */
@Mod(modid = Origin.MODID, name = Origin.MODNAME, version = Origin.VERSION,
	guiFactory = Origin.clientProxy,
	modLanguage = "scala"
)
object Origin extends IMod with IModResource {

	final val MODID = "origin"
	final val MODNAME = "Origin"
	final val VERSION = "@MOD_VERSION@"
	final val clientProxy = "temportalist.origin.internal.client.ProxyClient"
	final val serverProxy = "temportalist.origin.internal.server.ProxyServer"

	override def getModID: String = this.MODID

	override def getModName: String = this.MODNAME

	override def getModVersion: String = this.VERSION

	override def getDetails: IModDetails = this

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: ProxyCommon = null

	var dimensions: util.HashMap[String, Int] = new util.HashMap[String, Int]
	var dimensions1: util.HashMap[Int, String] = new util.HashMap[Int, String]

	private val tabItems: util.ArrayList[Item] = new util.ArrayList[Item]
	private val tabBlocks: util.ArrayList[Block] = new util.ArrayList[Block]

	def addItemToTab(item: Item): Unit = {
		tabItems.add(item)
	}

	def addBlockToTab(block: Block): Unit = {
		tabBlocks.add(block)
	}

	var placer: ItemPlacer = null

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		Registry.registerHandler(ExtendedEntityHandler, OptionHandler)
		super.preInitialize(this, event, this.proxy, CGOOptions)

		Registry.registerCommand(CommandOrigin)
		Registry.registerCommand(CommandOpLevel)

		this.registerNetwork()
		this.registerPacket(classOf[PacketExtendedSync.Handler],
			classOf[PacketExtendedSync], Side.CLIENT)
		this.registerPacket(classOf[Handler],
			classOf[PacketTeleport], Side.SERVER)
		this.registerPacket(classOf[PacketRedstoneUpdate.Handler],
			classOf[PacketRedstoneUpdate], Side.SERVER)
		this.registerPacket(classOf[PacketActionUpdate.Handler],
			classOf[PacketActionUpdate], Side.SERVER)
		this.registerPacket(classOf[PacketTileCallback.Handler],
			classOf[PacketTileCallback], null)
		this.registerPacket(classOf[PacketTriggerRadialSelection.Handler],
			classOf[PacketTriggerRadialSelection], Side.SERVER)

		this.placer = new ItemEgg(Origin.MODID, "placer")
		//WorldManipulation.preInit()

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event, this.proxy)
	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event, this.proxy)

		if (!this.tabItems.isEmpty || !this.tabBlocks.isEmpty) {
			val originTab: CreativeTabs = new CreativeTabs(Origin.MODID) {
				override def getTabIconItem: Item = {
					Items.carrot_on_a_stick
				}
			}

			for (index <- 0 until this.tabBlocks.size()) {
				this.tabBlocks.get(index).setCreativeTab(originTab)
			}
			for (index <- 0 until this.tabItems.size()) {
				this.tabItems.get(index).setCreativeTab(originTab)
			}

		}

		Origin.loadResource("buttonArrow", (EnumResource.GUI, "button.png"))

	}

	@Mod.EventHandler
	def serverLoad(event: FMLServerStartingEvent): Unit = {

		for (command <- Registry.getCommands) {
			event.registerServerCommand(command)
		}

		val allWS: Array[WorldServer] = DimensionManager.getWorlds
		val temp: util.HashMap[String, Integer] = new util.HashMap[String, Integer]

		for (i <- allWS.indices) {
			temp.put(allWS(i).provider.getDimensionName, allWS(i).provider.getDimensionId)
		}

		Origin.dimensions.clear()
		Origin.dimensions1.clear()
		val keys: util.SortedSet[String] = new util.TreeSet[String](temp.keySet)
		val iterator: util.Iterator[String] = keys.iterator()
		while (iterator.hasNext) {
			val key: String = iterator.next()
			val id: Int = temp.get(key)
			Origin.dimensions.put(key, id)
			Origin.dimensions1.put(id, key)
		}

	}

	private final val temportalist: UUID = UUID.fromString("dcb7f6a8-9f0d-4d6d-81f0-356e7b05f78f")
	private final val progwml6: UUID = UUID.fromString("83898b28-6118-4900-9137-41ffc46b6e10")

	/**
	 * SHHHHHHHH! SECRET PUMPKIN!
	 */
	@SubscribeEvent
	def onPlayerJoin(event: PlayerLoggedInEvent): Unit = {
		if (event.player.getGameProfile.getId.equals(this.progwml6)) {
			if (event.player.getCurrentArmor(3) == null) {
				val secretPumpkin: ItemStack = new ItemStack(Blocks.pumpkin, 1, 0)
				secretPumpkin.addEnchantment(Enchantment.unbreaking, 5)
				secretPumpkin.setStackDisplayName("Pumpkin of Awesomeness")
				event.player.setCurrentItemOrArmor(4, secretPumpkin)
			}
		}
	}

	@SubscribeEvent
	def serverChat(event: ServerChatEvent): Unit = {
		// NPEs because apparently CustomNPC's were setting some variable to null in the
		// if statement following this NPE check
		// https://github.com/TheTemportalist/Origin/issues/7
		if (event == null || event.player == null || event.player.getGameProfile == null ||
				event.player.getGameProfile.getId == null)
			return
		if (event.player.getGameProfile.getId.equals(this.temportalist)) {
			var color: EnumChatFormatting = null
			while (color == null) {
				color = EnumChatFormatting.values()(
					event.player.worldObj.rand.nextInt(EnumChatFormatting.values().length)
				)
				if (!color.isColor) color = null
			}
			event.getComponent.setChatStyle(new ChatStyle().setColor(color))
		}
	}

}

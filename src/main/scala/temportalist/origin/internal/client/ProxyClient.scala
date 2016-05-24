package temportalist.origin.internal.client

import java.util

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import temportalist.origin.api.common.resource.IModResource
import temportalist.origin.foundation.client.KeyHandler
import temportalist.origin.foundation.common.register.Registry
import temportalist.origin.internal.client.gui.{GuiConfig, HealthOverlay, GuiRadialMenuHandler}
import temportalist.origin.internal.common.ProxyCommon

/**
 *
 *
 * @author TheTemportalist
 */
class ProxyClient extends ProxyCommon with IModGuiFactory {

	override def preInit(): Unit = {
		Registry.getAllRegisteredBlocks.foreach(block => {
			if (block.hasCustomItemModel) {
				if (block.usesOBJ) OBJLoader.instance.addDomain(block.getModID)
				this.addCustomModel(Item.getItemFromBlock(block), 0, block.getCompoundName)
			}
		})
		Registry.registerHandler(KeyHandler)
	}



	// ~~~~~~~~~~~ Model Registration ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final def addCustomModel(mod: IModResource, block: Block, blockName: String): Unit = {
		this.addCustomModel(Item.getItemFromBlock(block), 0, mod.getModID + ":" + blockName)
	}

	final def addCustomModel(item: Item, meta: Int, location: String): Unit = {
		ModelLoader.setCustomModelResourceLocation(item, meta,
			new ModelResourceLocation(location, "inventory"))
	}

	// ~~~~~~~~~~~ Other Register Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def register(): Unit = {
		Registry.registerHandler(GuiRadialMenuHandler, HealthOverlay)

	}

	override def postInit(): Unit = {
		KeyHandler.registerAll()
	}

	// ~~~~~~~~~~~ GuiHandler ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		null
	}

	// ~~~~~~~~~~~ GuiConfig Factory ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = null

	override def initialize(minecraftInstance: Minecraft): Unit = {}

	override def getHandlerFor(
			element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = null

	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = classOf[GuiConfig]

}

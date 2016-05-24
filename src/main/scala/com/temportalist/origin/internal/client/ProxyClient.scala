package com.temportalist.origin.internal.client

import java.util

import com.temportalist.origin.api.client.utility.Rendering
import com.temportalist.origin.api.common.lib.LogHelper
import com.temportalist.origin.api.common.register.Registry
import com.temportalist.origin.api.common.rendering.ISpriteMapper
import com.temportalist.origin.internal.client.gui.{GuiConfig, GuiRadialMenuHandler, HealthOverlay}
import com.temportalist.origin.internal.common.{CGOOptions, Origin, ProxyCommon}
import cpw.mods.fml.client.IModGuiFactory
import cpw.mods.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.SoundCategory
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.client.event.TextureStitchEvent

import scala.collection.mutable.ListBuffer

/**
 *
 *
 * @author TheTemportalist
 */
class ProxyClient() extends ProxyCommon with IModGuiFactory {

	override def register(): Unit = {
		Registry.registerHandler(GuiRadialMenuHandler, HealthOverlay)

	}

	override def postInit(): Unit = {
		CGOOptions.volumeControls.foreach({ case (name: String, volume: Float) =>
			Rendering.mc.gameSettings.setSoundLevel(SoundCategory.func_147154_a(name), volume)
		})
	}

	val spritees: ListBuffer[ISpriteMapper] = new ListBuffer[ISpriteMapper]

	override def registerSpritee(spritee: ISpriteMapper): Unit = {
		spritees += spritee
	}

	@SubscribeEvent
	def pre_Sprites(event: TextureStitchEvent.Pre): Unit = {
		if (event.map.getTextureType == 1) // items only
			for (spritee: ISpriteMapper <- this.spritees) {
				LogHelper.info(Origin.MODNAME,
					"Loading sprite for " + spritee.getResourceLocation().toString)
				event.map.registerIcon(spritee.getResourceLocation().toString)
			}
	}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		null
	}

	override def initialize(minecraftInstance: Minecraft): Unit = {}

	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = {
		null
	}

	override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = {
		null
	}

	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = {
		classOf[GuiConfig]
	}
}

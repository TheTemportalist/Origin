package com.temportalist.origin.foundation.client.gui

import cpw.mods.fml.relauncher.{SideOnly, Side}
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.RenderGameOverlayEvent

/**
 *
 *
 * @author  TheTemportalist  5/18/15
 */
@SideOnly(Side.CLIENT)
trait IOverlay {

	def pre(event: RenderGameOverlayEvent.Pre, resolution: ScaledResolution): Unit = {}

	def post(event: RenderGameOverlayEvent.Post, resolution: ScaledResolution): Unit = {}

	def text(event: RenderGameOverlayEvent.Text): Unit = {}

	def chat(event: RenderGameOverlayEvent.Chat): Unit = {}

}

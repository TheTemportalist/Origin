package com.temportalist.origin.internal.client.gui

import java.util

import com.temportalist.origin.api.client.utility.Rendering
import com.temportalist.origin.api.common.register.Registry
import com.temportalist.origin.api.common.utility.Scala
import com.temportalist.origin.foundation.client.gui.IOverlay
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.RenderGameOverlayEvent
import scala.collection.mutable

/**
 *
 *
 * @author  TheTemportalist  5/18/15
 */
@SideOnly(Side.CLIENT)
object OverlayHandler {

	Registry.registerHandler(this)

	private val overlays = mutable.Map[EnumOverlay, util.List[IOverlay]](
		EnumOverlay.PRE -> new util.ArrayList[IOverlay](),
		EnumOverlay.POST -> new util.ArrayList[IOverlay](),
		EnumOverlay.TEXT -> new util.ArrayList[IOverlay](),
		EnumOverlay.CHAT -> new util.ArrayList[IOverlay]()
	)
	private var reso: ScaledResolution = null

	def register(overlay: IOverlay, types: Seq[EnumOverlay]): Unit = {
		types.foreach(typeOver => {
			this.overlays(typeOver).add(overlay)
		})
	}

	@SubscribeEvent
	def pre(event: RenderGameOverlayEvent.Pre): Unit = {
		this.reso = new ScaledResolution(Rendering.mc, Rendering.mc.displayWidth,
			Rendering.mc.displayHeight)
		Scala.iterateCol(this.overlays(EnumOverlay.PRE),
			(overlay: IOverlay) => overlay.pre(event, this.reso))
	}

	@SubscribeEvent
	def post(event: RenderGameOverlayEvent.Post): Unit = {
		Scala.iterateCol(this.overlays(EnumOverlay.POST),
			(overlay: IOverlay) => overlay.post(event, this.reso))
	}

	@SubscribeEvent
	def preText(event: RenderGameOverlayEvent.Text): Unit = {
		Scala.iterateCol(this.overlays(EnumOverlay.TEXT), (overlay: IOverlay) => overlay.text(event))
	}

	@SubscribeEvent
	def preChat(event: RenderGameOverlayEvent.Chat): Unit = {
		Scala.iterateCol(this.overlays(EnumOverlay.CHAT), (overlay: IOverlay) => overlay.chat(event))
	}

}

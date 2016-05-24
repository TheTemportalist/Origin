package temportalist.origin.internal.client.gui

import java.util

import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.api.client.utility.Rendering
import temportalist.origin.api.common.utility.Scala
import temportalist.origin.foundation.client.gui.IOverlay
import temportalist.origin.foundation.common.register.Registry

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
		this.reso = new ScaledResolution(Rendering.mc)
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

package temportalist.origin.foundation.client.gui

import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author  TheTemportalist  5/18/15
 */
@SideOnly(Side.CLIENT)
trait IOverlay {

	def pre(event: RenderGameOverlayEvent.Pre): Unit = {}

	def post(event: RenderGameOverlayEvent.Post): Unit = {}

	def text(event: RenderGameOverlayEvent.Text): Unit = {}

	def chat(event: RenderGameOverlayEvent.Chat): Unit = {}

}

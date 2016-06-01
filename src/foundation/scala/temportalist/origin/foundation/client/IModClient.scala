package temportalist.origin.foundation.client

import net.minecraftforge.client.event.{MouseEvent, RenderGameOverlayEvent}
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.api.client.EnumHUDOverlay
import temportalist.origin.foundation.client.gui.IOverlay
import temportalist.origin.foundation.client.modTraits.{IHasKeys, IHasMod}

import scala.collection.mutable.ListBuffer

/**
  * Implement this to automate the calling of client elements like KeyBinders and Overlays.
  * An object which extends this interface should have [[temportalist.origin.foundation.client.IModClient.preInit()]] called in the ProxyClient preInit.
  * Created by TheTemportalist on 4/9/2016.
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
trait IModClient extends IHasMod {

	/**
	  * This needs to be called in [[temportalist.origin.foundation.common.IProxy.preInit]]
	  */
	def preInit(): Unit = {
		this.getMod.registerHandler(this)
	}

	// ~~~~~~~~~~ KeyBinder ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@SubscribeEvent
	final def onMouse(event: MouseEvent): Unit = {
		this match {
			case keys: IHasKeys => keys.onMouseEvent(event)
			case _ =>
		}
	}

	@SubscribeEvent
	final def onKey(event: KeyInputEvent): Unit = {
		this match {
			case keys: IHasKeys => keys.onKeyEvent(event)
			case _ =>
		}
	}

	// ~~~~~~~~~~ Overlays ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private val overlays = Map[EnumHUDOverlay, ListBuffer[IOverlay]](
		EnumHUDOverlay.PRE -> ListBuffer[IOverlay](),
		EnumHUDOverlay.POST -> ListBuffer[IOverlay](),
		EnumHUDOverlay.TEXT -> ListBuffer[IOverlay](),
		EnumHUDOverlay.CHAT -> ListBuffer[IOverlay]()
	)

	final def registerOverlay(overlay: IOverlay, types: EnumHUDOverlay*): Unit = {
		types.foreach(this.overlays(_) += overlay)
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	final def pre(event: RenderGameOverlayEvent.Pre): Unit = {
		this.overlays(EnumHUDOverlay.PRE).foreach(_.pre(event))
	}

	@SubscribeEvent
	final def post(event: RenderGameOverlayEvent.Post): Unit = {
		this.overlays(EnumHUDOverlay.POST).foreach(_.post(event))
	}

	@SubscribeEvent
	final def preText(event: RenderGameOverlayEvent.Text): Unit = {
		this.overlays(EnumHUDOverlay.TEXT).foreach(_.text(event))
	}

	@SubscribeEvent
	final def preChat(event: RenderGameOverlayEvent.Chat): Unit = {
		this.overlays(EnumHUDOverlay.CHAT).foreach(_.chat(event))
	}

}

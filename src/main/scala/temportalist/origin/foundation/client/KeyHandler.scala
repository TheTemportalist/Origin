package temportalist.origin.foundation.client

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.common.{Loader, Optional}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.foundation.common.register.Registry

import scala.collection.mutable.ListBuffer

/**
 *
 *
 * @author  TheTemportalist  5/20/15
 */
@SideOnly(Side.CLIENT)
object KeyHandler {

	private val binders = new ListBuffer[IKeyBinder]()

	def register(binders: IKeyBinder*): Unit = {
		this.binders ++= binders
	}

	def registerAll(): Unit = {
		for (binder <- this.binders) binder.register()
	}

	def isNEKLoaded: Boolean = Loader.isModLoaded("notenoughkeys")

	@SubscribeEvent
	def onMouseEvent(event: MouseEvent): Unit = {
		if (!this.isNEKLoaded) this.checkKeys(event.button + 100)
	}

	@SubscribeEvent
	def onKeyEvent(event: KeyInputEvent): Unit = {
		if (!this.isNEKLoaded) this.checkKeys(-1)
	}

	@Optional.Method(modid = "notenoughkeys")
	@SubscribeEvent
	def onNEKEvent(event: modwarriors.notenoughkeys.api.KeyBindingPressedEvent): Unit = {
		this.onKeyPressed(event.keyBinding, event.isKeyBindingPressed)
	}

	private def checkKeys(keycode: Int): Unit = {
		this.binders.foreach(binder => binder.checkKeys(keycode))
	}

	private def onKeyPressed(binding: KeyBinding, isPressed: Boolean): Unit = {
		this.binders.foreach(binder => binder.sendPressed(binding, isPressed))
	}

}

package com.temportalist.origin.foundation.client

import com.temportalist.origin.api.common.register.Registry
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent
import cpw.mods.fml.common.{Loader, Optional}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.event.MouseEvent

import scala.collection.mutable.ListBuffer

/**
 *
 *
 * @author  TheTemportalist  5/20/15
 */
@SideOnly(Side.CLIENT)
object KeyHandler {

	Registry.registerHandler(this)

	private val binders = new ListBuffer[IKeyBinder]()

	def register(binder: IKeyBinder): Unit = {
		this.binders += binder
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
	def onNEKEvent(event: KeyBindingPressedEvent): Unit = {
		this.onKeyPressed(event.keyBinding, event.isKeyBindingPressed)
	}

	private def checkKeys(keycode: Int): Unit = {
		this.binders.foreach(binder => binder.checkKeys(keycode))
	}

	private def onKeyPressed(binding: KeyBinding, isPressed: Boolean): Unit = {
		this.binders.foreach(binder => binder.sendPressed(binding, isPressed))
	}

}

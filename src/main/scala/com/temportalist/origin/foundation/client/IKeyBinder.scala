package com.temportalist.origin.foundation.client

import com.temportalist.origin.api.common.resource.IModDetails
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.relauncher.{Side, SideOnly}
import modwarriors.notenoughkeys.api.Api
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.{Keyboard, Mouse}

import scala.collection.mutable

/**
 *
 *
 * @author  TheTemportalist  5/20/15
 */
@SideOnly(Side.CLIENT)
trait IKeyBinder {

	/**
	 * A map of all of this controller's keybindings mapped to the time the key started being down
	 */
	private val keys = mutable.Map[KeyBinding, Long]()

	def getMod: IModDetails

	protected def makeKeyBinding(unlocalizedName: String, key: Int,
			category: EnumKeyCategory): KeyBinding = {
		this.makeKeyBinding(unlocalizedName, key, category.getName)
	}

	protected def makeKeyBinding(unlocalizedName: String, key: Int,
			category: String): KeyBinding = {
		val binding = new KeyBinding(unlocalizedName, key, category)
		this.keys(binding) = 0L
		ClientRegistry.registerKeyBinding(binding)
		Api.registerMod(this.getMod.getModName, unlocalizedName)
		binding
	}

	def checkKeys(keycode: Int): Unit = {
		this.keys.keys.foreach(kb =>
			if (keycode < 0 || kb.getKeyCode == keycode) this.checkKeyAndSendPressed(kb)
		)
	}

	def checkKeyAndSendPressed(key: KeyBinding): Unit =
		this.sendPressed(key, this.isKeyDown(key.getKeyCode))

	def sendPressed(key: KeyBinding, isPressed: Boolean): Unit = {
		if (this.keys.contains(key) && isPressed) this.onKeyPressed(key)
	}

	def isKeyDown(key: Int): Boolean = {
		if (key < 0) Mouse.isButtonDown(key + 100)
		else Keyboard.isKeyDown(key)
	}

	def onKeyPressed(keyBinding: KeyBinding): Unit

}

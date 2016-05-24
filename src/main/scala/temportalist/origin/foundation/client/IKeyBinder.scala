package temportalist.origin.foundation.client

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.ModAPIManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.input.{Keyboard, Mouse}
import temportalist.origin.api.common.resource.IModDetails

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

	def getModName: String = this.getMod.getModName

	def register(): Unit

	protected def makeKeyBinding(unlocalizedName: String, key: Int,
			category: EnumKeyCategory): KeyBinding = {
		this.makeKeyBinding(unlocalizedName, key, category.getName)
	}

	protected def makeKeyBinding(unlocalizedName: String, key: Int,
			category: String): KeyBinding = {
		val binding = new KeyBinding(unlocalizedName, key, category)
		this.addKeyBinding(binding)
		binding
	}

	final def addKeyBindings(bindings: KeyBinding*): IKeyBinder = {
		bindings.foreach(this.addKeyBinding)
		this
	}

	final def addKeyBinding(binding: KeyBinding): IKeyBinder = {
		this.keys(binding) = 0L
		ClientRegistry.registerKeyBinding(binding)
		if (ModAPIManager.INSTANCE.hasAPI("API_NEK"))
			modwarriors.notenoughkeys.api.Api.registerMod(
				this.getModName, binding.getKeyDescription)
		this
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

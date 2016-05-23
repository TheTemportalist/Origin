package temportalist.origin.foundation.client

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.input.{Keyboard, Mouse}

import scala.collection.mutable

/**
  * Implement this interface to have keybindings automatically loaded on game init
  * Created by TheTemportalist on 4/9/2016.
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
trait IKeyBinder {

	private val keys = mutable.ListBuffer[KeyBinding]()

	/**
	  * Use this method to create and register your KeyBindings
	  * Make sure to call it in your [[temportalist.origin.foundation.common.IProxy.postInit]]
	  * Use [[registerKeyBinding]] to register a [[KeyBinding]]
	  */
	def register(): Unit

	/**
	  * Registers a KeyBinding with the client and adds it to the list of bindings for this binder
	  * @param keyBinding The [[KeyBinding]]
	  */
	final def registerKeyBinding(keyBinding: KeyBinding): Unit = {
		ClientRegistry.registerKeyBinding(keyBinding)
		this.keys += keyBinding
	}

	/**
	  * Checks to see if KeyBindings are pressed
	  * @param keycode The keycode to check bindings against
	  * @param checkDown If a KeyBinding should be checked for down
	  */
	final def checkBindingsForPress(keycode: Int, checkDown: Boolean = true): Unit = {
		this.keys.foreach(kb => {
			if ((keycode < 0 || kb.getKeyCode == keycode) &&
					(checkDown && this.isKeyCodeDown(kb.getKeyCode)))
				this.onKeyPressed(kb)
		})
	}

	/**
	  * Checks if a keycode is down
	  * @param keycode The keycode
	  * @return If the Mouse or Keyboard button is pressed
	  */
	final def isKeyCodeDown(keycode: Int): Boolean = {
		if (keycode < 0) Mouse.isButtonDown(keycode + 100)
		else Keyboard.isKeyDown(keycode)
	}

	/**
	  * Called when a KeyBinding is pressed
	  * @param keyBinding The [[KeyBinding]]
	  */
	def onKeyPressed(keyBinding: KeyBinding): Unit

}

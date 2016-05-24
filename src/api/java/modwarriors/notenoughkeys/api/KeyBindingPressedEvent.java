package modwarriors.notenoughkeys.api;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

/**
 * Called when a keybinding is triggered with the passed with valid modifiers
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
@Cancelable
public class KeyBindingPressedEvent extends Event {

	/**
	 * The KeyBinding being triggered
	 */
	public KeyBinding keyBinding = null;
	/**
	 * Tells whether a modifier was required AND was down when triggered
	 */
	public boolean shiftRequired = false, ctrlRequired = false, altRequired = false;
	/**
	 * Use this variable to check if your key is pressed. DO NOT check KeyBinding.getIsKeyPressed().
	 * That value will return only if the main key is pressed or not.
	 */
	public boolean isKeyBindingPressed;

	/**
	 * Called with the passed keyBinding and modifiers.
	 * Subscribe to this event so activate a keybinding when triggered.
	 *
	 * @param keyBinding The KeyBinding being triggered. Stores the key's description and keycode
	 * @param modifiers  The modifiers (SHIFT, CTRL, ALT) that determine when a compatible key is pressed
	 */
	public KeyBindingPressedEvent(KeyBinding keyBinding, boolean[] modifiers, boolean isPressed) {
		this.keyBinding = keyBinding;
		this.shiftRequired = modifiers[0];
		this.ctrlRequired = modifiers[1];
		this.altRequired = modifiers[2];
		this.isKeyBindingPressed = isPressed;
	}

}

package temportalist.origin.api.client.utility

import net.minecraft.client.Minecraft
import net.minecraftforge.fml.relauncher.{SideOnly, Side}
import org.lwjgl.input.{Keyboard, Mouse}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
object Keys {

	/*
	 * The true ctrl key(29 on mac) on mac is used for right clicking
	 */
	private final val shift1: Int = 42
	private final val shift2: Int = 54
	private final val ctrl1: Int = if (Minecraft.isRunningOnMac) 219 else 29
	private final val ctrl2: Int = if (Minecraft.isRunningOnMac) 220 else 157
	private final val alt1: Int = 56
	private final val alt2: Int = 184

	def isShiftKeyDown: Boolean = {
		Keyboard.isKeyDown(this.shift1) || Keyboard.isKeyDown(this.shift2)
	}

	def isCtrlKeyDown: Boolean = {
		Keyboard.isKeyDown(this.ctrl1) || Keyboard.isKeyDown(this.ctrl2)
	}

	def isAltKeyDown: Boolean = {
		Keyboard.isKeyDown(this.alt1) || Keyboard.isKeyDown(this.alt2)
	}

	def isShiftKey(keyCode: Int): Boolean = {
		keyCode == this.shift1 || keyCode == this.shift2
	}

	def isCtrlKey(keyCode: Int): Boolean = {
		keyCode == this.ctrl1 || keyCode == this.ctrl2
	}

	def isAltKey(keyCode: Int): Boolean = {
		keyCode == this.alt1 || keyCode == this.alt2
	}

	def isMouseDownLeft: Boolean = Mouse.isButtonDown(0)

	def isMouseDownRight: Boolean = Mouse.isButtonDown(0)

	def isMouseDownCenter: Boolean = Mouse.isButtonDown(0)

}

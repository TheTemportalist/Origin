package temportalist.origin.screwdriver.client

import com.temportalist.origin.foundation.client.gui.GuiRadialMenu
import GuiRadialMenu.RadialHandler
import com.temportalist.origin.api.client.utility.Rendering
import com.temportalist.origin.api.common.register.Registry
import com.temportalist.origin.foundation.client.gui.GuiRadialMenu
import com.temportalist.origin.internal.client.gui.GuiRadialMenuHandler
import com.temportalist.origin.screwdriver.common.AddonScrewdriver
import com.temportalist.origin.screwdriver.common.behaviors.BehaviorWrap
import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.InputEvent.{KeyInputEvent, MouseInputEvent}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.settings.KeyBinding
import net.minecraft.item.ItemStack
import org.lwjgl.input.Keyboard

/**
 *
 *
 * @author TheTemportalist 12/20/2015
 */
@SideOnly(Side.CLIENT)
class GuiScrewdriverRadial(stack: ItemStack)
		extends GuiRadialMenu(40, 100,
			AddonScrewdriver.NBTBehaviorHelper.getHotBarAsBehaviors(stack)) {

	override def shouldSelect(): Boolean =
		!Keyboard.isKeyDown(GuiScrewdriverRadial.openRadial.getKeyCode)

	override def getHandler: RadialHandler[BehaviorWrap] = GuiScrewdriverRadial

}

object GuiScrewdriverRadial extends RadialHandler[BehaviorWrap] {

	@SideOnly(Side.CLIENT)
	var openRadial: KeyBinding = _

	@SideOnly(Side.CLIENT)
	def registerClient(): Unit = {
		this.register()
		Registry.registerHandler(this)
		this.openRadial = new KeyBinding(
			"key.openRadial", Keyboard.KEY_K, "key.categories.gameplay"
		)
		ClientRegistry.registerKeyBinding(this.openRadial)
	}

	def register(): Unit = {
		GuiRadialMenu.register(this, classOf[BehaviorWrap])
	}

	override def getRadialFromGlobalID(globalID: Int): BehaviorWrap =
		AddonScrewdriver.getRadialBehaviorByGlobalID(globalID)

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	def keyPress(event: KeyInputEvent): Unit = {
		this.checkPressed()
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	def mousePress(event: MouseInputEvent): Unit = {
		this.checkPressed()
	}

	@SideOnly(Side.CLIENT)
	def checkPressed(): Unit = {
		if (Keyboard.isKeyDown(GuiScrewdriverRadial.openRadial.getKeyCode) &&
				Rendering.mc.currentScreen == null) {
			val stack: ItemStack = Rendering.mc.thePlayer.getCurrentEquippedItem
			if (stack != null && stack.getItem == AddonScrewdriver.screwdriver &&
					stack.getItemDamage > 0)
				GuiRadialMenuHandler.display(new GuiScrewdriverRadial(stack))
		}
	}

}

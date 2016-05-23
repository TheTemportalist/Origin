package temportalist.origin.foundation.client.modTraits

import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.foundation.client.IKeyBinder
import temportalist.origin.foundation.common.modTraits.IHasDetails

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
trait IHasKeys extends IHasDetails {

	@SideOnly(Side.CLIENT)
	def getKeyBinder: IKeyBinder

	@SideOnly(Side.CLIENT)
	final def onMouseEvent(event: MouseEvent): Unit = {
		this.getKeyBinder.checkBindingsForPress(event.getButton + 100)
	}

	@SideOnly(Side.CLIENT)
	final def onKeyEvent(event: KeyInputEvent): Unit = {
		this.getKeyBinder.checkBindingsForPress(-1)
	}

}

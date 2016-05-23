package temportalist.origin.internal.client

import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.origin.foundation.client.gui.GuiConfigBase
import temportalist.origin.internal.common.Origin

/**
  *
  * Created by TheTemportalist on 4/9/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
class GuiConfig(guiScreen: GuiScreen) extends GuiConfigBase(guiScreen, Origin) {}

package com.temportalist.origin.internal.client.gui

import com.temportalist.origin.foundation.client.gui.GuiConfigBase
import com.temportalist.origin.internal.common.Origin
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.GuiScreen

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiConfig(guiScreen: GuiScreen) extends GuiConfigBase(guiScreen, Origin, Origin.MODID) {}

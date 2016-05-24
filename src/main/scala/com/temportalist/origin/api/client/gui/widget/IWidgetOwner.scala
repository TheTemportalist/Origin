package com.temportalist.origin.api.client.gui.widget

import cpw.mods.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
trait IWidgetOwner {

	def getXSize: Int

	def getYSize: Int

	def getGuiLeft: Int

	def getGuiTop: Int

}

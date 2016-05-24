package com.temportalist.origin.api.common.lib

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer

/**
 *
 *
 * @author TheTemportalist
 */
trait IRadialSelection {

	@SideOnly(Side.CLIENT)
	def draw(mc: Minecraft, x: Double, y: Double, z: Double, w: Double, h: Double,
			renderText: Boolean): Unit

	def onSelection(player: EntityPlayer): Unit

	def getGlobalID: Int

}

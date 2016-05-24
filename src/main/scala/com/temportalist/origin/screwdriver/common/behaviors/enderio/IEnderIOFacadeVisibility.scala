package com.temportalist.origin.screwdriver.common.behaviors.enderio

import cpw.mods.fml.common.Optional
import crazypants.enderio.api.tool.IHideFacades
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

/**
 * Created by TheTemportalist on 12/20/2015.
 */
@Optional.Interface(
	iface = "crazypants.enderio.api.tool.IHideFacades", modid = "EnderIO", striprefs = true)
trait IEnderIOFacadeVisibility extends IHideFacades {

	def canHideFacades(stack: ItemStack): Boolean

	override def shouldHideFacades(stack: ItemStack,
			player: EntityPlayer): Boolean = canHideFacades(stack)

}

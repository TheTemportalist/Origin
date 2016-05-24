package com.temportalist.origin.screwdriver.common.behaviors.enderio

import com.temportalist.origin.screwdriver.common.behaviors.IWrench
import cpw.mods.fml.common.Optional
import crazypants.enderio.api.tool.IConduitControl
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

/**
 * Created by TheTemportalist on 12/20/2015.
 */
@Optional.Interface(
	iface = "crazypants.enderio.api.tool.IConduitControl", modid = "EnderIO", striprefs = true)
trait IEnderIOConduitVisibility extends IConduitControl {

	override def showOverlay(stack: ItemStack, player: EntityPlayer): Boolean = {
		stack.getItem match {
			case wrench: IWrench => wrench.canWrench(stack)
			case _ => false
		}
	}

}

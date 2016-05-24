package com.temportalist.origin.api.client.render

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.{ItemRenderType, ItemRendererHelper}

/**
 *
 *
 * @author TheTemportalist 4/13/15
 */
@SideOnly(Side.CLIENT)
trait ItemRender extends IItemRenderer {

	override def handleRenderType(item: ItemStack, `type`: ItemRenderType): Boolean = true

	override def shouldUseRenderHelper(`type`: ItemRenderType, item: ItemStack,
			helper: ItemRendererHelper): Boolean = true

}

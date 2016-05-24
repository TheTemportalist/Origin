package com.temportalist.origin.api.client.render

import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist 4/13/15
 */
abstract class TERenderItem(rl: ResourceLocation) extends TERenderer(rl) with ItemRender {

	override def renderItem(iType: ItemRenderType, item: ItemStack, data: AnyRef*): Unit = {
		GL11.glPushMatrix()

		if (iType == ItemRenderType.INVENTORY) GL11.glTranslated(0, -0.1, 0)
		else if (iType == ItemRenderType.EQUIPPED_FIRST_PERSON) GL11.glTranslated(0.5, 0.5, 0.5)
		else if (iType == ItemRenderType.EQUIPPED) GL11.glTranslated(0.5, 0.5, 0.5)

		this.renderTileEntityAt(this.getRenderingTileItem, -0.5, -0.5, -0.5, 0)

		GL11.glPopMatrix()
	}

	def getRenderingTileItem: TileEntity

}
